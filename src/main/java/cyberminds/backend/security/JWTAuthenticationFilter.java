package cyberminds.backend.security;


import cyberminds.backend.dto.request.LoginDTO;
import cyberminds.backend.dto.request.ResponseDTO;
import cyberminds.backend.dto.request.UnsuccessfulLogin;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import com.auth0.jwt.JWT;

import static cyberminds.backend.security.SecurityConstant.*;


@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    LoginDTO credential = new LoginDTO();

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext context) {
        this.authenticationManager = authenticationManager;
        userRepository = context.getBean(UserRepository.class);
        setFilterProcessesUrl("/api/users/login");
    }

    @ExceptionHandler(AppException.class)
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            credential = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword(), new ArrayList<>()));
        } catch (IOException exception) {
            throw new RuntimeException("Bad credential. User does not exist.");
        }
    }

    @ExceptionHandler(AppException.class)
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET.getBytes(StandardCharsets.UTF_8)));
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDto;
        String email = ((User) authResult.getPrincipal()).getUsername();
        cyberminds.backend.model.user.User user = userRepository.findByEmail(email);

        cyberminds.backend.model.user.User appUser = userRepository.findUserById(user.getId());
        responseDto = new ResponseDTO();
        responseDto.setEmail(appUser.getEmail());

        responseDto.setCreatedDate(LocalDateTime.now().toString());
        responseDto.setId(appUser.getId());
        responseDto.setModifiedDate(LocalDateTime.now().toString());
        responseDto.setModifiedDate(LocalDateTime.now().toString());
        responseDto.setToken(token);

        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        response.getOutputStream().print("{ \"Object data\": " + objectMapper.writeValueAsString(responseDto) + "}");
        response.flushBuffer();
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, jakarta.servlet.ServletException {
        super.unsuccessfulAuthentication((jakarta.servlet.http.HttpServletRequest) request, (jakarta.servlet.http.HttpServletResponse) response, failed);
        UnsuccessfulLogin responseDetails = new UnsuccessfulLogin(LocalDateTime.now(), "Login error. Incorrect email or password.", "Bad request", "/user/login");
        response.getOutputStream().print("{ \"Server response\": " + responseDetails + "}");
    }
}