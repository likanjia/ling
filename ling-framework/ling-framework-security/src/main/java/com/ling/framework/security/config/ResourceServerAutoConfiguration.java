package com.ling.framework.security.config;


import com.ling.framework.security.converter.CustomAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

@Configuration
public class ResourceServerAutoConfiguration {

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> authenticationTokenConverter(OAuth2AuthorizationService authorizationService) {
        return new CustomAuthenticationConverter(authorizationService);
    }
}
