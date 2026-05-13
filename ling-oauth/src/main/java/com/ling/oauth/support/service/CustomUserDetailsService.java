package com.ling.oauth.support.service;

import com.ling.framework.core.result.R;
import com.ling.oauth.client.RemoteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final RemoteUserService remoteUserService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("查询用户...");

        UserDetails userDetails = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN", "USER")
                .authorities("sys:list", "sys:add", "sys:update", "sys:delete","ROLE_ADMIN","ROLE_USER")
                .build();

        R list = remoteUserService.list();
        System.out.println("远程访问："+list);


        return userDetails;
    }
}
