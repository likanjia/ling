package com.ling.framework.security.converter;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.Principal;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final OAuth2AuthorizationService authorizationService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        OAuth2Authorization authorization = authorizationService.findByToken(jwt.getTokenValue(), OAuth2TokenType.ACCESS_TOKEN);

        if (Objects.isNull(authorization)) {
            throw new InvalidBearerTokenException(jwt.getTokenValue());
        }

        Object principal = authorization.getAttributes().get(Principal.class.getName());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;

        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);

        if (CollUtil.isNotEmpty(authorities)) {
            authorities.addAll(usernamePasswordAuthenticationToken.getAuthorities());
        }

        return new UsernamePasswordAuthenticationToken(usernamePasswordAuthenticationToken.getPrincipal(), usernamePasswordAuthenticationToken.getCredentials(), authorities);
    }
}
