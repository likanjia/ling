package com.ling.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * <p>限流配置类
 * <p>用于定义基于客户端IP地址的限流Key解析器，实现基于IP维度的请求频率限制。
 * <p>url: https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webflux/gatewayfilter-factories/requestratelimiter-factory.html
 */

@Configuration
public class RateLimiterConfiguration {


    /**
     * 创建基于客户端IP地址的限流Key解析器
     *
     * <p>该Bean用于Spring Cloud Gateway的限流功能，通过提取客户端的IP地址作为限流的唯一标识（Key），
     * 实现基于IP维度的请求频率限制。</p>
     *
     * <p>实现逻辑：
     * <ol>
     *   <li>从GatewayFilterExchange中获取当前请求对象</li>
     *   <li>通过getRequest().getRemoteAddress()获取客户端连接地址信息</li>
     *   <li>提取InetAddress对象并获取其字符串形式的IP地址</li>
     *   <li>将IP地址封装为Mono流返回，供限流过滤器使用</li>
     * </ol>
     *
     * @return KeyResolver实例，用于解析限流的唯一标识（客户端IP）
     * @throws NullPointerException 如果远程地址为空时抛出
     */
    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        // 获取客户端IP地址作为限流Key
        return exchange -> Mono
                .just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }
}
