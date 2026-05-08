package com.ling.framework.security.model;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
public class OAuth2AuthorizationEntity {


    private String id;

    /**
     * 注册客户端ID
     */
    private String registeredClientId;

    /**
     * 主体名称
     */
    private String principalName;

    /**
     * 授权类型
     */
    private String authorizationGrantType;

    /**
     * 授权范围
     */
    private Set<String> authorizedScopes;

    /**
     * 属性数据（JSON格式）
     */
    private Map<String, Object> attributes;

    /**
     * 状态值
     */
    private String state;

//    ================== 授权码 ==================

    /**
     * 授权码值
     */
    private String authorizationCodeValue;

    /**
     * 授权码颁发时间
     */
    private Date authorizationCodeIssuedAt;

    /**
     * 授权码过期时间
     */
    private Date authorizationCodeExpiresAt;

    /**
     * 授权码元数据
     */
    private Map<String,Object> authorizationCodeMetadata;

//    ================== 访问令牌 ==================
    /**
     * 访问令牌值
     */
    private String accessTokenValue;

    /**
     * 访问令牌颁发时间
     */
    private Date accessTokenIssuedAt;

    /**
     * 访问令牌过期时间
     */

    private Date accessTokenExpiresAt;

    /**
     * 访问令牌元数据
     */
    private Map<String,Object> accessTokenMetadata;

    /**
     * 访问令牌类型
     */
    private String accessTokenType;

    /**
     * 访问令牌范围
     */

    private Set<String> accessTokenScopes;

//    ================== OIDC ID令牌 ==================
    /**
     * OIDC ID令牌值
     */

    private String oidcIdTokenValue;

    /**
     * OIDC ID令牌颁发时间
     */

    private Date oidcIdTokenIssuedAt;

    /**
     * OIDC ID令牌过期时间
     */

    private Date oidcIdTokenExpiresAt;

    /**
     * OIDC ID令牌元数据
     */

    private Map<String,Object> oidcIdTokenMetadata;

//    ================== 刷新令牌 ==================
    /**
     * 刷新令牌值
     */
    private String refreshTokenValue;

    /**
     * 刷新令牌颁发时间
     */

    private Date refreshTokenIssuedAt;

    /**
     * 刷新令牌过期时间
     */
    private Date refreshTokenExpiresAt;

    /**
     * 刷新令牌元数据
     */

    private Map<String,Object> refreshTokenMetadata;

//    ================== 用户代码 ==================
    /**
     * 用户代码值
     */

    private String userCodeValue;

    /**
     * 用户代码颁发时间
     */

    private Date userCodeIssuedAt;

    /**
     * 用户代码过期时间
     */

    private Date userCodeExpiresAt;

    /**
     * 用户代码元数据
     */

    private Map<String,Object> userCodeMetadata;

//    ================== 设备代码 ==================
    /**
     * 设备代码值
     */

    private String deviceCodeValue;

    /**
     * 设备代码颁发时间
     */

    private Date deviceCodeIssuedAt;

    /**
     * 设备代码过期时间
     */

    private Date deviceCodeExpiresAt;

    /**
     * 设备代码元数据
     */

    private Map<String,Object> deviceCodeMetadata;
}
