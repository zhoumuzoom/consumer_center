# OAuth2 SSO接入说明

本系统保留原有的用户名密码登录，同时提供 OAuth2 SSO 换票接口。SSO 校验成功后，系统仍然签发自己的 Sa-Token `accessToken` 和 `refreshToken`，后续业务接口不直接信任门户 Token。

## 后端配置

在环境变量中配置：

```bash
SSO_OAUTH2_ENABLED=true
SSO_OAUTH2_INTROSPECTION_URI=https://portal.example.com/oauth2/introspect
SSO_OAUTH2_CLIENT_ID=consumer-center
SSO_OAUTH2_CLIENT_SECRET=change-me
SSO_OAUTH2_EXTERNAL_ID_FIELD=sub
SSO_OAUTH2_SOURCE=portal
```

校验请求遵循 OAuth2 Token Introspection 约定：

- `POST` 到 `SSO_OAUTH2_INTROSPECTION_URI`；
- `Content-Type: application/x-www-form-urlencoded`；
- 表单字段：`token`、`token_type_hint=access_token`；
- 如果配置了 client-id，则使用 HTTP Basic 认证；
- 响应需要包含 `active=true` 和 `sub`（或配置的 `external-id-field`）。

系统按 `system_user.external_id + system_user.source` 查找本地用户。用户需要预先绑定门户用户标识，未知用户默认拒绝登录。

## SSO换票接口

```http
POST /api/auth/sso-login
Content-Type: application/json

{
  "externalToken": "portal-oauth2-access-token"
}
```

成功响应与本地登录一致，返回 `accessToken`、`refreshToken` 及其闲置有效期。接口免本系统 Token 认证。

## iframe接入

将 iframe 指向：

```text
https://consumer-center.example.com/sso/callback
```

推荐门户在 iframe 加载完成后通过 `postMessage` 发送一次性 OAuth2 访问令牌：

```javascript
iframe.contentWindow.postMessage(
  { type: 'consumer-center-sso', accessToken: portalAccessToken },
  'https://consumer-center.example.com'
)
```

本系统前端通过 `VITE_SSO_PARENT_ORIGIN` 限制允许的门户来源，例如：

```bash
VITE_SSO_PARENT_ORIGIN=https://portal.example.com
```

也支持 OAuth2 隐式回调常见的 URL 参数 `access_token` 或 URL Fragment 参数，但不建议在 URL 中传递长期有效 Token；优先使用 `postMessage` 或一次性短时效票据。

本地登录仍使用 `/login` 页面，不受 SSO 开关影响。
