package org.sefglobal.invoker.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.sefglobal.invoker.exception.HTTPClientCreationException;
import org.sefglobal.invoker.utill.InvokerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
public class InvokerController {
    private Logger logger = LoggerFactory.getLogger(InvokerController.class);

    @RequestMapping("/api/**")
    private String login(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        return "hi";
    }

    @RequestMapping("/open/api/**")
    private void sendRequestToOpenApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String gatewayUri = "http://localhost:8080";
        // extract the uri from the request
        String[] uriArr = request.getRequestURI().split("/", 5);
        if (uriArr.length != 5) {
            logger.warn("Bad Request, uri or method not found. Attribute length: " + uriArr.length);
            response.sendError(400, "Bad Request, uri or method not found");
            return;
        }
        String uri = "/" + uriArr[4];
        if (request.getQueryString() != null)
            uri += "?" + request.getQueryString();
        uri = gatewayUri + uri;

        // extract other data from request
        String method = request.getMethod();
        String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String contentType = request.getContentType();

        if (contentType == null || contentType.isEmpty()) contentType = ContentType.APPLICATION_JSON.toString();

        HttpRequestBase executor;
        if ("GET".equalsIgnoreCase(method)) {
            executor = new HttpGet(uri);
        } else if ("POST".equalsIgnoreCase(method)) {
            executor = new HttpPost(uri);
            StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
            ((HttpPost) executor).setEntity(payloadEntity);
        } else if ("PUT".equalsIgnoreCase(method)) {
            executor = new HttpPut(uri);
            StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
            ((HttpPut) executor).setEntity(payloadEntity);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            executor = new HttpDelete(uri);
        } else {
            logger.warn("Unexpected HTTP Method: " + method);
            response.sendError(400, "Bad Request, method not supported");
            return;
        }

        String result = execute(executor, request, response);
        if (result != null && !result.isEmpty()) response.getWriter().write(result);
    }

    private String execute(HttpRequestBase executor, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpResponse response;
        try {
            response = InvokerUtil.execute(executor, 3);
        } catch (HTTPClientCreationException e) {
            resp.sendError(500, "Internal Server Error");
            return null;
        }
        BufferedReader rd;
        if (response != null) {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
        } else {
            resp.sendError(500, "Unable to renew tokens");
            return null;
        }
        StringBuilder resultBuffer = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultBuffer.append(line);
        }
        String result = resultBuffer.toString();
        resp.setStatus(response.getStatusLine().getStatusCode());
        rd.close();
        return result;
    }
}
