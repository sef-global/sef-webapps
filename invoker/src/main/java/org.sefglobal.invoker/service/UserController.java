package org.sefglobal.invoker.service;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;
import org.sefglobal.invoker.dto.AuthData;
import org.sefglobal.invoker.util.Constants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/user")
    private void getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        // Send unauthorized response if user not logged-in
        if (session == null || session.getAttribute(Constants.ATTR_TOKEN) == null) {
            response.sendError(401, "Unauthorized, Access token not found in the session");
            return;
        }
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setCharacterEncoding(Consts.UTF_8.name());
        JSONObject responseObject = new JSONObject();
        // Add username to the payload
        responseObject.put("username", ((AuthData)session.getAttribute(Constants.ATTR_TOKEN)).getUsername());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(responseObject.toString());
        }
    }
}
