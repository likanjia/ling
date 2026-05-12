package com.ling.framework.security.converter;

import com.ling.framework.security.model.OAuth2AuthorizationEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.hutool.core.convert.Convert.convert;
import static cn.hutool.core.convert.Convert.toDate;

@UtilityClass
public class OAuth2AuthorizationConverter {
    /**
     * 将OAuth2Authorization转换为OAuth2AuthorizationEntity
     * @param authorization OAuth2授权对象
     * @return OAuth2授权实体
     */
    public OAuth2AuthorizationEntity toEntity(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");

        OAuth2AuthorizationEntity entity = new OAuth2AuthorizationEntity();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());

        Set<String> scopes = authorization.getAuthorizedScopes();
        if (scopes != null && !scopes.isEmpty()) {
            entity.setAuthorizedScopes(scopes);
        }

        Map<String, Object> attributes = authorization.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            try {
                entity.setAttributes(attributes);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize attributes", e);
            }
        }

        Object state = attributes != null ? attributes.get(OAuth2ParameterNames.STATE) : null;
        if (state != null) {
            entity.setState(state.toString());
        }

        // 处理授权码
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode != null) {
            OAuth2AuthorizationCode code = authorizationCode.getToken();
            entity.setAuthorizationCodeValue(code.getTokenValue());
            entity.setAuthorizationCodeIssuedAt(toDate(code.getIssuedAt()));
            entity.setAuthorizationCodeExpiresAt(toDate(code.getExpiresAt()));
            entity.setAuthorizationCodeMetadata(handlerMetadata(authorizationCode.getMetadata(),true));
        }

        // 处理访问令牌
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            OAuth2AccessToken token = accessToken.getToken();
            entity.setAccessTokenValue(token.getTokenValue());
            entity.setAccessTokenIssuedAt(toDate(token.getIssuedAt()));
            entity.setAccessTokenExpiresAt(toDate(token.getExpiresAt()));
            entity.setAccessTokenType(token.getTokenType().getValue());
            Set<String> tokenScopes = token.getScopes();
            if (tokenScopes != null && !tokenScopes.isEmpty()) {
                entity.setAccessTokenScopes(tokenScopes);
            }

            Map<String, Object> metadata = handlerMetadata(accessToken.getMetadata(),true);

            entity.setAccessTokenMetadata(metadata);
        }

        // 处理刷新令牌
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization
                .getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            OAuth2RefreshToken token = refreshToken.getToken();
            entity.setRefreshTokenValue(token.getTokenValue());
            entity.setRefreshTokenIssuedAt(toDate(token.getIssuedAt()));
            entity.setRefreshTokenExpiresAt(toDate(token.getExpiresAt()));
            entity.setRefreshTokenMetadata(handlerMetadata(refreshToken.getMetadata(),true));
        }

        return entity;
    }

    /**
     * 将OAuth2AuthorizationEntity转换为OAuth2Authorization
     * @param entity OAuth2授权实体
     * @return OAuth2授权对象
     */
    public OAuth2Authorization fromEntity(OAuth2AuthorizationEntity entity) {
        Assert.notNull(entity, "entity cannot be null");

        RegisteredClient registeredClient = RegisteredClient.withId(entity.getRegisteredClientId())
                .clientId(entity.getRegisteredClientId())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()))
                .build();

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()));

        if (entity.getAuthorizedScopes() != null && !entity.getAuthorizedScopes().isEmpty()) {
            builder.authorizedScopes(entity.getAuthorizedScopes());
        }

        if (entity.getAttributes() != null && !entity.getAttributes().isEmpty()) {
            builder.attributes(attributes -> attributes.putAll(entity.getAttributes()));
        }

        // 处理授权码
        if (entity.getAuthorizationCodeValue() != null) {
            String codeValue = entity.getAuthorizationCodeValue();
            Date issuedAt = entity.getAuthorizationCodeIssuedAt();
            Date expiresAt = entity.getAuthorizationCodeExpiresAt();
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(codeValue,
                    convert(Instant.class,issuedAt), convert(Instant.class,expiresAt));

            Map<String, Object> metadata = handlerMetadata(entity.getAuthorizationCodeMetadata(),false);
            builder.token(authorizationCode, metadataBuilder -> {
                if (metadata != null) {
                    metadataBuilder.putAll(metadata);
                }
            });
        }

        // 处理访问令牌
        if (entity.getAccessTokenValue() != null) {
            String tokenValue = entity.getAccessTokenValue();
            OAuth2AccessToken.TokenType tokenType = OAuth2AccessToken.TokenType.BEARER;
            if (entity.getAccessTokenType() != null) {
                tokenType = new OAuth2AccessToken.TokenType(entity.getAccessTokenType());
            }
            Date issuedAt = entity.getAccessTokenIssuedAt();
            Date expiresAt = entity.getAccessTokenExpiresAt();

            Set<String> scopes = entity.getAccessTokenScopes();

            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue,
                    convert(Instant.class,issuedAt), convert(Instant.class,expiresAt), scopes);

            Map<String, Object> metadata = handlerMetadata(entity.getAccessTokenMetadata(),false);

            builder.token(accessToken, metadataBuilder -> {
                metadataBuilder.putAll(metadata);
            });
        }

        // 处理刷新令牌
        if (entity.getRefreshTokenValue() != null) {
            String tokenValue = entity.getRefreshTokenValue();
            Date issuedAt = entity.getRefreshTokenIssuedAt();
            Date expiresAt = entity.getRefreshTokenExpiresAt();
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(tokenValue,
                    convert(Instant.class,issuedAt), convert(Instant.class,expiresAt));

            Map<String, Object> metadata = handlerMetadata(entity.getRefreshTokenMetadata(),false);
            builder.token(refreshToken, metadataBuilder -> {
                if (metadata != null) {
                    metadataBuilder.putAll(metadata);
                }
            });
        }

        return builder.build();
    }

    private Map<String, Object> handlerMetadata(Map<String, Object> metadata,boolean isToDate) {
        if (metadata == null) {
            return null;
        }
        Map<String,Object> claims = (Map<String, Object>)metadata.get("metadata.token.claims");
        if (claims == null) {
            return metadata;
        }
        Map<String, Object> mutableClaims = new HashMap<>(claims);

        if (isToDate) {
            mutableClaims.put("iat",toDate(claims.get("iat")));
            mutableClaims.put("nbf",toDate(claims.get("nbf")));
            mutableClaims.put("exp",toDate(claims.get("exp")));
        } else {

            mutableClaims.put("iat",convert(Instant.class,claims.get("iat")));
            mutableClaims.put("nbf",convert(Instant.class,claims.get("nbf")));
            mutableClaims.put("exp",convert(Instant.class,claims.get("exp")));
        }
        Map<String, Object> mutableMetadata = new HashMap<>(metadata);
        mutableMetadata.put("metadata.token.claims", mutableClaims);
        return mutableMetadata;
    }
}
