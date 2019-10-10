package org.sefglobal.invoker.utill;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.sefglobal.invoker.exception.HTTPClientCreationException;

import java.io.IOException;

public final class InvokerUtil {

    private static final Log log = LogFactory.getLog(InvokerUtil.class);

    public static HttpResponse execute(HttpRequestBase executor, int retryCount)
            throws IOException, HTTPClientCreationException {
        if (retryCount == 0) {
            return null;
        }

        CloseableHttpClient client = ControllerUtility.getHTTPClient();

        return client.execute(executor);
    }

    public static String printResponse(HttpResponse response){
        String responseString = "";
        try {
            responseString = new BasicResponseHandler().handleResponse(response);
        } catch (IOException e) {
            log.error("Error while printing response!");
        }
        return responseString;
    }
}
