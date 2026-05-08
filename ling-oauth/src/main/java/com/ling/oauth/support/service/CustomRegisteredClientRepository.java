package com.ling.oauth.support.service;


import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

//@Component
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {

        if (!"admin".equals(clientId)) {
            return null;
        }

        TokenSettings tokenSettings = TokenSettings.builder()
                //每次使用 refresh token 换取新的 access token 时，会生成一个新的 refresh token
                .reuseRefreshTokens(false)
                .build();

        return RegisteredClient.withId(clientId)
                .clientId("admin")
                .clientSecret("{noop}admin")
                .scopes(scopes -> {
                    scopes.add("server");
                    scopes.add("app");
                })
                .authorizationGrantTypes(grantTypes -> {
                    grantTypes.add(new AuthorizationGrantType("password"));
                    grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .tokenSettings(tokenSettings)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();


    }
}
