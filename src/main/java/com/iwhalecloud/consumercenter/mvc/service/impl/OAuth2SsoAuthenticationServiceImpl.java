package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.mapper.SystemUserMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.SystemUser;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.service.LoginSessionService;
import com.iwhalecloud.consumercenter.mvc.service.SsoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Objects;

/**
 * OAuth2 Token Introspection SSO实现
 */
@Service
public class OAuth2SsoAuthenticationServiceImpl implements SsoAuthenticationService {

    private static final Byte USER_STATUS_ENABLED = 1;

    private final RestClient restClient = createRestClient();

    @Value("${sso.oauth2.enabled:false}")
    private boolean enabled;

    @Value("${sso.oauth2.introspection-uri:}")
    private String introspectionUri;

    @Value("${sso.oauth2.client-id:}")
    private String clientId;

    @Value("${sso.oauth2.client-secret:}")
    private String clientSecret;

    @Value("${sso.oauth2.external-id-field:sub}")
    private String externalIdField;

    @Value("${sso.oauth2.source:portal}")
    private String source;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private LoginSessionService loginSessionService;

    @Override
    public LoginVO login(String externalToken) {
        if (!StringUtils.hasText(externalToken)) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_TOKEN_INVALID);
        }
        if (!enabled || !StringUtils.hasText(introspectionUri)) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_NOT_CONFIGURED);
        }
        Map<String, Object> claims = introspect(externalToken);
        if (!isActive(claims)) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_TOKEN_INVALID);
        }

        String externalId = getClaimAsString(claims, externalIdField);
        if (!StringUtils.hasText(externalId)) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_TOKEN_INVALID);
        }

        SystemUser user = systemUserMapper.selectOne(new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getExternalId, externalId)
                .eq(SystemUser::getSource, source));
        if (user == null) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_USER_NOT_FOUND);
        }
        if (!Objects.equals(USER_STATUS_ENABLED, user.getStatus())) {
            throw new BusinessException(ErrorMessageEnum.AUTH_USER_DISABLED);
        }
        return loginSessionService.issueTokens(user.getId());
    }

    private Map<String, Object> introspect(String externalToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", externalToken);
        form.add("token_type_hint", "access_token");

        try {
            return restClient.post()
                    .uri(introspectionUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(this::setClientAuthentication)
                    .body(form)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            throw new BusinessException(ErrorMessageEnum.AUTH_SSO_PROVIDER_UNAVAILABLE);
        }
    }

    private void setClientAuthentication(HttpHeaders headers) {
        if (StringUtils.hasText(clientId)) {
            headers.setBasicAuth(clientId, clientSecret == null ? "" : clientSecret);
        }
    }

    private boolean isActive(Map<String, Object> claims) {
        if (claims == null) {
            return false;
        }
        Object active = claims.get("active");
        return Boolean.TRUE.equals(active) || "true".equalsIgnoreCase(String.valueOf(active));
    }

    private String getClaimAsString(Map<String, Object> claims, String field) {
        Object value = claims.get(field);
        return value == null ? null : value.toString();
    }

    private static RestClient createRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
