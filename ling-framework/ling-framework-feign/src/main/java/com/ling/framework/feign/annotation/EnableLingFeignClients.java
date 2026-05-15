package com.ling.framework.feign.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Ling 自定义 FeignClients 启用注解。
 * <p>
 * 组合了 {@link EnableFeignClients}，默认扫描 {@code com.ling} 包下所有 {@code @FeignClient} 接口，
 * 业务模块启动类只需贴此注解即可，无需再手动维护 basePackages 列表。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableFeignClients
public @interface EnableLingFeignClients {

    /**
     * 扫描的包路径，默认 com.ling，可覆盖。
     */
    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackages")
    String[] basePackages() default {"com.ling"};
}
