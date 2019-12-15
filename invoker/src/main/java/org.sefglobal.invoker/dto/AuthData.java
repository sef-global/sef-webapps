package org.sefglobal.invoker.dto;

import java.io.Serializable;

public class AuthData implements Serializable {

    private static final long serialVersionUID = -7212868256388049980L;

    private String clientCredentials;
    private String accessToken;
    private String refreshToken;
    private String username;

    public String getClientCredentials() {
        return clientCredentials;
    }

    public void setClientCredentials(String clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
