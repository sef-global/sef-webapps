package org.sefglobal.webapps.partnership.redirector.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.sefglobal.webapps.partnership.redirector.exception.PartnershipRedirectorException;
import org.sefglobal.webapps.partnership.redirector.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Redirector {
    @GetMapping("/{key}")
    private ModelAndView redirectToEventUrl(@PathVariable String key, HttpServletRequest request) throws PartnershipRedirectorException {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(key);
            String decodedString = new String(decodedBytes);

            String[] linkDetails = decodedString.split(":");
            if (linkDetails.length != 2) {
                throw new ResourceNotFoundException("Incorrect Url");
            }

            int eventId = Integer.parseInt(linkDetails[0]);
            int societyId = Integer.parseInt(linkDetails[1]);

            Map<String, Object> elements = new HashMap<>();
            elements.put("eventId", eventId);
            elements.put("societyId", societyId);
            elements.put("ip", getClientIp(request));
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(elements);

            final String uri = "http://localhost:8080/engagements";

            HttpClient httpclient = HttpClients.createDefault();
            HttpPost executor = new HttpPost(uri);
            StringEntity payloadEntity = new StringEntity(json, ContentType.create("application/json"));
            executor.setEntity(payloadEntity);

            HttpResponse response = httpclient.execute(executor);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200 && entity != null) {
                try (InputStream instream = entity.getContent()) {
                    BufferedReader rd = rd = new BufferedReader(new InputStreamReader(instream, StandardCharsets.UTF_8));
                    StringBuilder resultBuffer = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        resultBuffer.append(line);
                    }
                    String result = resultBuffer.toString();
                    Map<String, Object> mapObj = new Gson().fromJson(
                            result, new TypeToken<HashMap<String, Object>>() {
                            }.getType()
                    );
                    String redirectUrl = (String) mapObj.get("url");
                    return new ModelAndView("redirect:" + redirectUrl);
                }
            } else {
                throw new ResourceNotFoundException("Sorry, broken link");
            }

        } catch (IllegalArgumentException | IOException e) {
            throw new ResourceNotFoundException("Sorry, broken link");
        }
    }


    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}
