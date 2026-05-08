package com.ling.framework.security.config;


import com.ling.framework.security.converter.CustomAuthenticationConverter;
import com.ling.framework.security.handler.ResourceAccessDeniedHandler;
import com.ling.framework.security.handler.ResourceAuthExceptionEntryPoint;
import com.ling.framework.security.util.RSAUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ResourceServerConfiguration {

    private final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/index/idx").hasRole("ADMIN"));

        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(new ResourceAccessDeniedHandler());
                    ex.authenticationEntryPoint(new ResourceAuthExceptionEntryPoint());
                });

        http.oauth2ResourceServer(resourceServer -> {
            resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                    .authenticationEntryPoint(new ResourceAuthExceptionEntryPoint());
        });

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        RSAPublicKey rsaPublicKey = RSAUtils.getRSAPublicKey();
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("http://localhost:18082");
        jwtDecoder.setJwtValidator(withIssuer);

        return jwtDecoder;
    }

}
