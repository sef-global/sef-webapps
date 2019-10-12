package org.sefglobal.invoker.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.sefglobal.invoker.dto.Token;
import org.sefglobal.invoker.exception.HTTPClientCreationException;
import org.sefglobal.invoker.exception.UnexpectedResponseException;
import org.sefglobal.invoker.util.Constants;
import org.sefglobal.invoker.util.ControllerUtility;
import org.sefglobal.invoker.util.OAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/login")
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("email");
        String password = req.getParameter("password");
        if (username == null || password == null) {
            ControllerUtility.sendFailureRedirect(req, resp);
            return;
        }
        Token token;
        try {
            token = OAuthUtil.generateToken(username, password, "default");
        } catch (ParseException | ConnectException | HTTPClientCreationException e) {
            logger.error(e.getMessage());
            resp.sendError(500, "Internal Server Error: " + e.getMessage());
            return;
        } catch (UnexpectedResponseException e) {
            int statusCode = e.getHttpResponse().getStatusLine().getStatusCode();
            logger.error("Unexpected resp. Code: " + statusCode);
            resp.sendError(statusCode, "Error: " + e.getMessage());
            return;
        }

        if (token == null) {
            logger.error("Cannot create token for user: " + username);
            resp.sendError(500, "Internal Server Error, Cannot create token for user: " + username);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            session = req.getSession(true);
        }
        session.setAttribute(Constants.ATTR_TOKEN, token);
        session.setAttribute(Constants.ATTR_USER_NAME, username);
        String returnUri = req.getParameter("ret");

        JSONObject resultObj = new JSONObject();
        String redirectPath;

        if (returnUri != null) {
            if (returnUri.equals(req.getContextPath() + "/")) {
                redirectPath = req.getContextPath() + "/dashboard.jsp";
            } else {
                String queryStr = req.getParameter("q");
                redirectPath = (queryStr != null) ? (returnUri) + "?" + URLDecoder.decode(queryStr, "UTF-8") : (returnUri);
            }
        } else {
            redirectPath = req.getContextPath() + "/dashboard.jsp";
        }

        resultObj.put("redirect_path", redirectPath);

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(resultObj.toString());
        out.flush();

    }
}
