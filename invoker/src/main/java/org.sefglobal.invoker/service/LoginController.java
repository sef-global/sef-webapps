package org.sefglobal.invoker.service;

import org.sefglobal.invoker.util.ControllerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/login")
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            ControllerUtility.sendFailureRedirect(request, response);
            return;
        }


    }
}
