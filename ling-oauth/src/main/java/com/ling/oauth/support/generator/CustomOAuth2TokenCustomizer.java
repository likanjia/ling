package com.ling.oauth.support.generator;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Arrays;
import java.util.List;

public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {

        JwtClaimsSet.Builder claims = context.getClaims();

        List<String> list = Arrays.asList("admin", "user", "update");

        claims.claim("roles",list);
    }
}
