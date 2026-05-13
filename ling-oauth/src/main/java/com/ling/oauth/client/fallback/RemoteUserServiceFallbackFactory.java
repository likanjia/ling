package com.ling.oauth.client.fallback;

import com.ling.framework.core.result.R;
import com.ling.oauth.client.RemoteUserService;
import org.springframework.cloud.openfeign.FallbackFactory;

public class RemoteUserServiceFallbackFactory implements FallbackFactory<RemoteUserService> {
    @Override
    public RemoteUserService create(Throwable cause) {
        return new RemoteUserService() {

            @Override
            public R list() {
                throw new RuntimeException("降级（异常：" + cause.getMessage() + "），用户服务不可用");
            }
        };
    }
}
