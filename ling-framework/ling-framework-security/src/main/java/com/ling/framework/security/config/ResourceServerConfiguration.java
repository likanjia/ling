package com.ling.framework.security.config;


import cn.hutool.core.collection.ListUtil;
import com.ling.framework.security.handler.ResourceAccessDeniedHandler;
import com.ling.framework.security.handler.ResourceAuthExceptionEntryPoint;
import com.ling.framework.security.properties.Oauth2Properties;
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
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ResourceServerConfiguration {

    private final Oauth2Properties oauth2Properties;
    private final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;
    private final ResourceAccessDeniedHandler accessDeniedHandler;

    private final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    private static final List<String> DEFAULT_IGNORE_URLS = ListUtil.toList("/actuator/**", "/error", "/v3/api-docs");

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {

        configure(http);


        Boolean clientEnable = oauth2Properties.getClientEnable();
        if (Objects.isNull(clientEnable) || Objects.equals(clientEnable, Boolean.TRUE)) {
            http.oauth2ResourceServer(resourceServer -> {
                resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                                .decoder(jwtDecoder()))
                        .authenticationEntryPoint(resourceAuthExceptionEntryPoint);
            });
        }


        return http.build();
    }

    private void configure(HttpSecurity http) throws Exception {
        Boolean enabled = oauth2Properties.getEnabled();
        if (Objects.equals(enabled,Boolean.FALSE)) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return;
        }

        String[] permitMatchers = Objects.isNull(enabled) ? DEFAULT_IGNORE_URLS.toArray(new String[0]) : getPermitMatchers();
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(permitMatchers).permitAll()
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(resourceAuthExceptionEntryPoint);
                    ex.accessDeniedHandler(accessDeniedHandler);
                });;

    }


    private String[] getPermitMatchers() {
        List<String> customIgnoreUrls = oauth2Properties.getIgnoreUrl();
        List<String> ignoreUrl = Stream.concat(
                DEFAULT_IGNORE_URLS.stream(),
                customIgnoreUrls != null ? customIgnoreUrls.stream() : Stream.empty()
        ).distinct().toList();
        return ignoreUrl.toArray(new String[0]);
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
