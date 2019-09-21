package org.sefglobal.webapps.partnership.redirector.exception;

public class PartnershipRedirectorException extends Exception{
    public PartnershipRedirectorException() {
        super();
    }

    public PartnershipRedirectorException(String msg) {
        super(msg);
    }

    public PartnershipRedirectorException(String msg, Throwable e) {
        super(msg, e);
    }
}
