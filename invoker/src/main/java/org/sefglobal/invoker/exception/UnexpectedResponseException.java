package org.sefglobal.invoker.exception;

import org.apache.http.HttpResponse;

public class UnexpectedResponseException extends Exception {

    private HttpResponse httpResponse;

    public UnexpectedResponseException(HttpResponse httpResponse, String message) {
        super(message);
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
