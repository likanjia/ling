package com.ling.oauth.support.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.core.convert.Convert.toDate;

/**
 * 自定义 OAuth2 认证成功处理器
 * 用于处理 OAuth2 认证成功后的事件，构建并返回访问令牌响应
 */
public class CustomAuthenticationSuccessEventHandler implements AuthenticationSuccessHandler {

    // 访问令牌响应消息转换器，用于将 OAuth2AccessTokenResponse 转换为 HTTP 响应
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

    /**
     * 处理认证成功事件
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param authentication 认证对象
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("认证成功....");

        // 检查认证对象是否为 OAuth2AccessTokenAuthenticationToken 类型
        if (!(authentication instanceof OAuth2AccessTokenAuthenticationToken accessTokenAuthentication)) {
            // 如果不是，创建服务器错误并抛出异常
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "无法处理访问令牌响应。", null);
            throw new OAuth2AuthenticationException(error);
        }

        // 从认证对象中获取访问令牌
        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        // 从认证对象中获取刷新令牌
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        // 从认证对象中获取附加参数
        Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

        // 创建访问令牌响应构建器，设置令牌值、令牌类型和作用域
        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                .tokenType(accessToken.getTokenType())
                .scopes(accessToken.getScopes());

        // 如果访问令牌有发行时间和过期时间，计算过期时间（秒）
        if (Objects.nonNull(accessToken.getIssuedAt()) && Objects.nonNull(accessToken.getExpiresAt())) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }

        // 如果存在刷新令牌，添加到响应中
        if (Objects.nonNull(refreshToken)) {
            builder.refreshToken(refreshToken.getTokenValue());
        }

        // 如果存在附加参数，添加到响应中
        if (!CollectionUtils.isEmpty(additionalParameters)) {

            Map<String, Object> responseParameters = new HashMap<>(additionalParameters);
            additionalParameters.forEach((key,value) -> {
                if (value instanceof Instant) {
                    responseParameters.put(key, toDate(value));
                }
            });

            builder.additionalParameters(responseParameters);
        }

        // 构建访问令牌响应
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        // 创建 Servlet 服务器 HTTP 响应包装器
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        // 将访问令牌响应写入 HTTP 响应
        this.accessTokenResponseConverter.write(accessTokenResponse, null, httpResponse);
    }
}