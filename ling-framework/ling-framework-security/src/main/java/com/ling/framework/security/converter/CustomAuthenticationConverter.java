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

    /**
     * 将 JWT 令牌转换为认证令牌，合并 JWT 中的权限与原始授权中的权限
     *
     * @param jwt 待转换的 JWT 令牌
     * @return 包含合并后权限信息的认证令牌
     * @throws InvalidBearerTokenException 当根据 JWT 令牌值未找到对应的授权信息时抛出
     */
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
