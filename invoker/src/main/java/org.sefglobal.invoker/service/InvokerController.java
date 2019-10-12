package org.sefglobal.invoker.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.sefglobal.invoker.dto.Token;
import org.sefglobal.invoker.exception.HTTPClientCreationException;
import org.sefglobal.invoker.util.Constants;
import org.sefglobal.invoker.util.InvokerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
public class InvokerController {
    private Logger logger = LoggerFactory.getLogger(InvokerController.class);
    private static final int urlSplitLimit = 5; // production - 5 , testing - 4
    @Autowired
    private Environment environment;

    @RequestMapping("/api/**")
    private void sendRequestToApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.ATTR_TOKEN) == null) {
            response.sendError(401, "Unauthorized, Access token not found in the session");
            return;
        }

        final String gatewayUri = environment.getProperty("config.gatewayUri");
        // extract the uri from the request
        String[] uriArr = request.getRequestURI().split("/", urlSplitLimit-1);
        if (uriArr.length != urlSplitLimit-1) {
            logger.warn("Bad Request, uri or method not found. Attribute length: " + uriArr.length);
            response.sendError(400, "Bad Request, uri or method not found");
            return;
        }
        String uri = "/" + uriArr[urlSplitLimit-2];
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

        String result = execute(executor, request, response, false);
        if (result != null && !result.isEmpty()) response.getWriter().write(result);
    }

    @RequestMapping("/open/api/**")
    private void sendRequestToOpenApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String gatewayUri = environment.getProperty("config.gatewayUri");
        // extract the uri from the request
        String[] uriArr = request.getRequestURI().split("/", urlSplitLimit);
        if (uriArr.length != urlSplitLimit) {
            logger.warn("Bad Request, uri or method not found. Attribute length: " + uriArr.length);
            response.sendError(400, "Bad Request, uri or method not found");
            return;
        }
        String uri = "/" + uriArr[urlSplitLimit-1];
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

        String result = execute(executor, request, response, true);
        if (result != null && !result.isEmpty()) response.getWriter().write(result);
    }

    private String execute(HttpRequestBase executor, HttpServletRequest req, HttpServletResponse resp, boolean isOpenRequest)
            throws IOException {

        HttpResponse response;
        try {
            if(isOpenRequest){
                response = InvokerUtil.execute(executor, 3);
            }else{
                HttpSession session = req.getSession(false);
                Token token = (Token) session.getAttribute(Constants.ATTR_TOKEN);
                response = InvokerUtil.execute(executor, 3, token);
            }
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
