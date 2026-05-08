package com.ling.oauth.support.password;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.util.*;

public class PasswordGrantAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final AuthorizationGrantType authorizationGrantType;

    @Getter
    private final Set<String> scopes;

    @Getter
    private final Authentication clientPrincipal;

    @Getter
    private final Map<String, Object> additionalParameters;


    public PasswordGrantAuthenticationToken(AuthorizationGrantType authorizationGrantType, Set<String> scopes,
                                            Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.notNull(authorizationGrantType, "authorizationGrantType 不能为空");
        Assert.notNull(clientPrincipal, "clientPrincipal 不能为空");
        this.authorizationGrantType = authorizationGrantType;
        this.scopes = Collections.unmodifiableSet((scopes != null) ? new HashSet<>(scopes) : Collections.emptySet());;
        this.clientPrincipal = clientPrincipal;
        this.additionalParameters = Collections.unmodifiableMap(
                (additionalParameters != null) ? new HashMap<>(additionalParameters) : Collections.emptyMap());
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }
}
