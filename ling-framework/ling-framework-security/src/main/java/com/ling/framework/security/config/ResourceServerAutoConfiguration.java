package com.ling.framework.security.config;


import com.ling.framework.security.converter.CustomAuthenticationConverter;
import com.ling.framework.security.handler.ResourceAccessDeniedHandler;
import com.ling.framework.security.handler.ResourceAuthExceptionEntryPoint;
import com.ling.framework.security.properties.Oauth2Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.web.access.AccessDeniedHandler;


@EnableConfigurationProperties(Oauth2Properties.class)
public class ResourceServerAutoConfiguration {

    @Bean
    public ResourceAuthExceptionEntryPoint authenticationEntryPoint() {
        return new ResourceAuthExceptionEntryPoint();
    }

    @Bean
    public ResourceAccessDeniedHandler accessDeniedHandler() {

        return new ResourceAccessDeniedHandler();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> authenticationTokenConverter(OAuth2AuthorizationService authorizationService) {
        return new CustomAuthenticationConverter(authorizationService);
    }
}
