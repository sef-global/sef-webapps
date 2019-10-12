package org.sefglobal.invoker.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.reference.DefaultEncoder;
import org.sefglobal.invoker.exception.HTTPClientCreationException;
import org.sefglobal.invoker.exception.UnexpectedResponseException;
import org.sefglobal.invoker.service.LoginController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public final class ControllerUtility {

    private static final Log log = LogFactory.getLog(LoginController.class);

    public static Response executeRequest(HttpRequestBase executor, HttpSession session){
        HttpResponse response;
        try {
            response = InvokerUtil.execute(executor, 3);
        } catch (HTTPClientCreationException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        BufferedReader rd;
        if (response != null) {
            try {
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to renew tokens").build();
        }

        StringBuilder resultBuffer = new StringBuilder();
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            String line;
            while ((line = rd.readLine()) != null) {
                resultBuffer.append(line);
            }
            rd.close();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        String result = resultBuffer.toString();
        return Response.status(Response.Status.fromStatusCode(statusCode)).entity(result).build();
    }

    public static String executePost(HttpPost post) throws IOException, HTTPClientCreationException,
            UnexpectedResponseException {
        CloseableHttpClient client = getHTTPClient();
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
            return result.toString();
        }
        throw new UnexpectedResponseException(response, "Unexpected response: " + result.toString());
    }

    public static CloseableHttpClient getHTTPClient() throws HTTPClientCreationException {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
                    builder.build());
            return HttpClients.custom().setSSLSocketFactory(
                    sslSF).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error(e.getMessage(), e);
            throw new HTTPClientCreationException("Error occurred while retrieving http client", e);
        }
    }

    public static String sanitize(String url) throws EncodingException {

        Encoder encoder = new DefaultEncoder(new ArrayList<>());
        //first canonicalize
        String clean = encoder.canonicalize(url).trim();
        //then url decode
        clean = encoder.decodeFromURL(clean);

        //detect and remove any existent \r\n == %0D%0A == CRLF to prevent HTTP Response Splitting
        int idxR = clean.indexOf('\r');
        int idxN = clean.indexOf('\n');

        if (idxN >= 0 || idxR >= 0) {
            if (idxN > idxR) {
                //just cut off the part after the LF
                clean = clean.substring(0, idxN - 1);
            } else {
                //just cut off the part after the CR
                clean = clean.substring(0, idxR - 1);
            }
        }

        //re-encode again
        return encoder.encodeForURL(clean);
    }

    public static void sendFailureRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String referer = req.getHeader("referer");
        String redirect = (referer == null || referer.isEmpty()) ? req.getRequestURI() : referer;
        if (!redirect.contains("status")) {
            if (redirect.contains("?")) {
                redirect += "&status=fail";
            } else {
                redirect += "?status=fail";
            }
        }
        resp.sendRedirect(redirect);
    }

}
