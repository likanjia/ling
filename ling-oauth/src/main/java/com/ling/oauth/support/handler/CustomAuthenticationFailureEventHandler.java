package com.ling.oauth.support.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 OAuth2 认证失败处理器
 * 用于处理 OAuth2 认证失败后的事件，以 JSON 格式输出错误信息
 */
public class CustomAuthenticationFailureEventHandler implements AuthenticationFailureHandler {
    
    // Jackson 对象映射器，用于将对象转换为 JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理认证失败事件
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param exception 认证异常
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        System.out.println("认证失败.....");

        // 设置响应状态码为 401 (未授权)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");
        
        // 创建错误响应映射
        Map<String, Object> errorResponse = new HashMap<>();
        
        // 检查异常是否为 OAuth2AuthenticationException 类型
        if (exception instanceof OAuth2AuthenticationException oAuth2Exception) {
            // 获取 OAuth2 错误对象
            OAuth2Error error = oAuth2Exception.getError();
            // 设置错误码
            errorResponse.put("error", error.getErrorCode());
            // 设置错误描述
            errorResponse.put("error_description", error.getDescription());
            // 设置错误 URI（如果有）
            if (error.getUri() != null) {
                errorResponse.put("error_uri", error.getUri());
            }
        } else {
            // 非 OAuth2 异常，使用通用错误信息
            errorResponse.put("error", "unauthorized");
            errorResponse.put("error_description", exception.getMessage());
        }
        
        // 将错误响应映射转换为 JSON 并写入响应流
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}