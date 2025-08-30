package com.bensiebert.codelib.auth;

import com.bensiebert.codelib.auth.aspects.AuthenticationInterceptor;
import com.bensiebert.codelib.auth.aspects.CurrentUserArgumentResolver;
import com.bensiebert.codelib.auth.data.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CurrentUserArgumentResolver argumentResolver;

    @Autowired
    private TokenRepository tokens;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new AuthenticationInterceptor(tokens))
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver);
    }
}
