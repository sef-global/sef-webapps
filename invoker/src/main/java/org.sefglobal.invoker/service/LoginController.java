package org.sefglobal.invoker.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.sefglobal.invoker.dto.AuthData;
import org.sefglobal.invoker.exception.HTTPClientCreationException;
import org.sefglobal.invoker.exception.UnexpectedResponseException;
import org.sefglobal.invoker.util.Constants;
import org.sefglobal.invoker.util.ControllerUtility;
import org.sefglobal.invoker.util.OAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URLDecoder;

@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/logout")
    private void logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @PostMapping("/login")
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            ControllerUtility.sendFailureRedirect(request, response);
            return;
        }
        AuthData authData;
        try {
            authData = OAuthUtil.generateToken(username, password, "default");
        } catch (ParseException | ConnectException | HTTPClientCreationException e) {
            logger.error(e.getMessage());
            response.sendError(500, "Internal Server Error: " + e.getMessage());
            return;
        } catch (UnexpectedResponseException e) {
            int statusCode = e.getHttpResponse().getStatusLine().getStatusCode();
            logger.error("Unexpected resp. Code: " + statusCode);
            response.sendError(statusCode, "Error: " + e.getMessage());
            return;
        }

        if (authData == null) {
            logger.error("Cannot create token for user: " + username);
            response.sendError(500, "Internal Server Error, Cannot create token for user: " + username);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }
        session.setAttribute(Constants.ATTR_TOKEN, authData);
        String returnUri = request.getParameter("ret");

        JSONObject resultObj = new JSONObject();
        String redirectPath;

        if (returnUri != null) {
            if (returnUri.equals(request.getContextPath() + "/")) {
                redirectPath = request.getContextPath() + "/dashboard.jsp";
            } else {
                String queryStr = request.getParameter("q");
                redirectPath = (queryStr != null) ? (returnUri) + "?" + URLDecoder.decode(queryStr, "UTF-8") : (returnUri);
            }
        } else {
            redirectPath = request.getContextPath() + "/dashboard.jsp";
        }

        resultObj.put("redirect_path", redirectPath);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(resultObj.toString());
        out.flush();
    }
}
