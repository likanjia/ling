package com.ling.oauth.client;

import com.ling.framework.core.result.R;
import com.ling.oauth.client.fallback.RemoteUserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ling-upms-server", contextId = "RemoteUserService", fallbackFactory = RemoteUserServiceFallbackFactory.class)
public interface RemoteUserService {

    @GetMapping("/sys/user/list")
    R list();
}
