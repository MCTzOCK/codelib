package com.bensiebert.codelib.auth.aspects;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
import com.bensiebert.codelib.auth.primitive.BearerToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenRepository tokens;

    public AuthenticationInterceptor(TokenRepository tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)) return true;

        HandlerMethod hm = (HandlerMethod) handler;

        Authenticated ann = hm.getMethodAnnotation(Authenticated.class);

        if(ann == null) ann = hm.getBeanType().getAnnotation(Authenticated.class);

        if(ann == null) return true;

        String raw = request.getHeader("Authorization");
        String token = BearerToken.extract(raw);

        if(token.isEmpty() || token.isBlank() || !tokens.existsById(token)) {
            return reject(response, "Missing or invalid token.");

        }

        Token tkn = tokens.findById(token).orElseThrow();
        if(tkn.getExpiryTime() < System.currentTimeMillis()) {
            return reject(response, "Missing or invalid token.");
        }

        String[] required = ann.roles();
        String userRole = tkn.getUser().getRole();
        boolean ok = false;

        if(userRole.equalsIgnoreCase("admin")) {
            ok = true;
        }

        for(String role : required) {
            if(userRole.equalsIgnoreCase(role)) {
                ok = true;
                break;
            }
        }

        if(!ok) {
            return reject(response, "Insufficient permissions.");
        }

        request.setAttribute("user", tkn.getUser());

        if(!ann.customMethod().isBlank()) {
            Class clazz = hm.getBeanType();
            Method method = clazz.getMethod(ann.customMethod(), tkn.getUser().getClass());
            if(method == null) {
                return reject(response, "Invalid custom authentication method.");
            }

            if(method.getReturnType() != boolean.class) {
                return reject(response, "Invalid custom authentication method.");
            }

            boolean result = (boolean) method.invoke(hm.getBean(), tkn.getUser());

            if(!result) {
                return reject(response, "Custom authentication method returned false.");
            }
        }
        return true;
    }

    private boolean reject(HttpServletResponse res, String msg) throws Exception {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write(
                "{\"error\": \"unauthorized\", \"message\": \"" + msg + "\"}"
        );
        res.getWriter().flush();
        return false;
    }
}
