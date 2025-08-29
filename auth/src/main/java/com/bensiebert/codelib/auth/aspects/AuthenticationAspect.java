package com.bensiebert.codelib.auth.aspects;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.BearerToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthenticationAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private TokenRepository tokens;

    @Before(value = "@annotation(authenticated)", argNames = "jp,authenticated")
    public void checkAuthentication(JoinPoint jp, Authenticated authenticated) {
        try {
            String token = BearerToken.extract(request.getHeader("Authorization"));
            if (token.isEmpty() || token.isBlank()) {
                throw new RuntimeException("Invalid or missing authentication token.");
            }

            if (!tokens.existsById(token)) {
                throw new RuntimeException("Invalid or missing authentication token.");
            }

            Token tkn = tokens.findById(token).orElse(null);

            if (tkn == null || tkn.getExpiryTime() < System.currentTimeMillis()) {
                throw new RuntimeException("Invalid or missing authentication token.");
            }

            String[] required = authenticated.roles();

            User u = tkn.getUser();

            boolean hasRole = false;

            for (String role : required) {
                if (u.getRole().toLowerCase().equals(role.toLowerCase())) {
                    hasRole = true;
                    break;
                }
            }

            if (!hasRole) {
                throw new RuntimeException("Insufficient permissions to access this resource.");
            }

            request.setAttribute("user", tkn.getUser());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                String json = String.format("{\"error\": \"%s\"}", e.getMessage().replace("\"", "\\\""));
                response.getWriter().write(json);
                response.getWriter().flush();
            } catch (Exception _) {

            }
        }
    }
}
