package com.ling.framework.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("ling.security.oauth2")
public class Oauth2Properties {

    /**
     * 是否启用资源服务器
     */
    private Boolean enabled;

    /**
     * 是否启用客户端认证
     */
    private Boolean clientEnable;

    /**
     * 忽略的URL
     */
    private List<String> ignoreUrl;
}
