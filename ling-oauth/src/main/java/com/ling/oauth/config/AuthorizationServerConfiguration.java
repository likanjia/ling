package com.ling.oauth.config;


import com.ling.framework.security.handler.ResourceAuthExceptionEntryPoint;
import com.ling.framework.security.util.RSAUtils;
import com.ling.oauth.support.generator.CustomOAuth2RefreshTokenGenerator;
import com.ling.oauth.support.generator.CustomOAuth2TokenCustomizer;
import com.ling.oauth.support.handler.CustomAuthenticationFailureEventHandler;
import com.ling.oauth.support.handler.CustomAuthenticationSuccessEventHandler;
import com.ling.oauth.support.password.PasswordGrantAuthenticationConverter;
import com.ling.oauth.support.password.PasswordGrantAuthenticationProvider;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter;

@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfiguration {

    private final UserDetailsService userDetailsService;

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerFilter(HttpSecurity http) throws Exception {

        http.securityMatcher("/oauth2/**");

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        http.with(authorizationServerConfigurer,authorizationServer -> {

            // 配置令牌端点
            authorizationServer.tokenEndpoint(tokenEndpoint -> {
                tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter())
                        .authenticationProvider(
                                new PasswordGrantAuthenticationProvider(
                                        oAuth2AuthorizationService,
                                        tokenGenerator(),
                                        new ProviderManager(new DaoAuthenticationProvider(userDetailsService)))
                        )
                        .accessTokenResponseHandler(new CustomAuthenticationSuccessEventHandler())
                        .errorResponseHandler(new CustomAuthenticationFailureEventHandler());
            });

            //配置客户端认证
            authorizationServer.clientAuthentication(clientAuthentication -> {
                clientAuthentication.errorResponseHandler(new CustomAuthenticationFailureEventHandler());
            });

            // 配置授权服务器设置
            authorizationServer.authorizationServerSettings(AuthorizationServerSettings.builder().issuer("http://localhost:18082").build());

        });

        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());

        // 配置异常处理
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(new ResourceAuthExceptionEntryPoint()));

        return http.build();
    }

    private AuthenticationConverter accessTokenRequestConverter() {

        return new DelegatingAuthenticationConverter(new PasswordGrantAuthenticationConverter());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(jwkSource());
    }

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = RSAUtils.getRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {

        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder());
        jwtGenerator.setJwtCustomizer(new CustomOAuth2TokenCustomizer());


        CustomOAuth2RefreshTokenGenerator refreshTokenGenerator = new CustomOAuth2RefreshTokenGenerator(jwtEncoder());
        refreshTokenGenerator.setJwtCustomizer(new CustomOAuth2TokenCustomizer());

        return new DelegatingOAuth2TokenGenerator(jwtGenerator,refreshTokenGenerator);
    }
}
