package cyberminds.backend.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RestAccessDeniedHandler implements AuthenticationFailureHandler {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, IOException {
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", "Incorrect email or password, try again.");
        responseObj.put("status", HttpStatus.UNAUTHORIZED.toString());
        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, responseObj);
    }
}