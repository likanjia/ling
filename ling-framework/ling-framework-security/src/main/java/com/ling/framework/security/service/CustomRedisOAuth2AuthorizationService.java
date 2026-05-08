package com.ling.framework.security.service;


import com.ling.framework.core.util.RedisUtils;
import com.ling.framework.security.converter.OAuth2AuthorizationConverter;
import com.ling.framework.security.model.OAuth2AuthorizationEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//@Component
public class CustomRedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final static Long TIMEOUT = 10L;

    private static final String AUTHORIZATION = "token";


    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "授权不能为空");

        OAuth2AuthorizationEntity entity = OAuth2AuthorizationConverter.toEntity(authorization);

        if (isState(authorization)) {
            String token = authorization.getAttribute(OAuth2ParameterNames.STATE);
            RedisUtils.set(buildKey(OAuth2ParameterNames.STATE,token),entity,TIMEOUT,TimeUnit.SECONDS);
        }

        if (isCode(authorization)) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
            OAuth2AuthorizationCode codeToken = authorizationCode.getToken();
            long between = ChronoUnit.MINUTES.between(codeToken.getIssuedAt(),
                    codeToken.getExpiresAt());
            RedisUtils.set(buildKey(OAuth2ParameterNames.CODE,codeToken.getTokenValue()),entity,between,TimeUnit.SECONDS);
        }


        if (isAccessToken(authorization)) {
            OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
            long between = ChronoUnit.SECONDS.between(accessToken.getIssuedAt(),
                    accessToken.getExpiresAt());
            RedisUtils.set(buildKey(OAuth2ParameterNames.ACCESS_TOKEN,accessToken.getTokenValue()),entity,between,TimeUnit.SECONDS);
        }

        if (isRefreshToken(authorization)) {
            OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
            long between = ChronoUnit.SECONDS.between(refreshToken.getIssuedAt(),
                    refreshToken.getExpiresAt());
            RedisUtils.set(buildKey(OAuth2ParameterNames.REFRESH_TOKEN,refreshToken.getTokenValue()),entity,between, TimeUnit.SECONDS);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {

    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        throw new UnsupportedOperationException("不支持根据ID查询授权");
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        Assert.notNull(tokenType, "tokenType cannot be null");
        OAuth2AuthorizationEntity entity = RedisUtils.get(buildKey(tokenType.getValue(), token));
        return OAuth2AuthorizationConverter.fromEntity(entity);
    }

    /**
     * 构建key
     * @param type 类型
     * @param id ID
     * @return 拼接后的key字符串
     */
    private String buildKey(String type, String id) {
        return String.format("%s::%s::%s", AUTHORIZATION, type, id);
    }

    /**
     * 检查授权对象是否包含state属性
     * @param authorization 授权对象
     * @return 如果包含state属性返回true，否则返回false
     */
    private static boolean isState(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    /**
     * 检查授权对象是否包含授权码
     * @param authorization 授权对象
     * @return 如果包含授权码返回true，否则返回false
     */
    private static boolean isCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        return Objects.nonNull(authorizationCode);
    }

    /**
     * 判断授权是否包含刷新令牌
     * @param authorization 授权信息
     * @return 如果包含刷新令牌返回true，否则返回false
     */
    private static boolean isRefreshToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getRefreshToken());
    }

    /**
     * 判断授权对象是否包含访问令牌
     * @param authorization 授权对象
     * @return 如果包含访问令牌返回true，否则返回false
     */
    private static boolean isAccessToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAccessToken());
    }
}
