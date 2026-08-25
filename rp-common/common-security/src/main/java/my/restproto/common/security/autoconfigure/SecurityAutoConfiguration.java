package my.restproto.common.security.autoconfigure;

import my.restproto.common.redis.autoconfigure.RedisAutoConfiguration;
import my.restproto.common.restful.autoconfigure.RestfulAutoConfiguration;
import my.restproto.common.security.config.LazydogJwtSecretConfig;
import my.restproto.common.security.config.SecurityConfig;
import my.restproto.common.security.core.AccessDecideInterceptor;
import my.restproto.common.security.core.JwtTokenFilter;
import my.restproto.common.security.core.TokenHeaderResolver;
import my.restproto.common.security.core.access.AccessRuleSet;
import my.restproto.common.security.core.jwt.JwtTokenService;
import my.restproto.common.security.exception.AuthExceptionHandler;
import my.restproto.common.security.exception.DeniedHandler;
import my.restproto.common.security.exception.UnauthHandler;
import my.restproto.common.security.properties.AccessProps;
import my.restproto.common.security.properties.JwtTokenIssueProps;
import my.restproto.common.security.properties.LazydogJwtProps;
import my.restproto.common.security.properties.TokenHeaderProps;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * security 领域自动配置
 */
@AutoConfigureAfter({
        RedisAutoConfiguration.class,
        RestfulAutoConfiguration.class,
})
@EnableConfigurationProperties({
        TokenHeaderProps.class,
        JwtTokenIssueProps.class,
        LazydogJwtProps.class,
        AccessProps.class
})
@Import({
        UnauthHandler.class,
        DeniedHandler.class,
        AuthExceptionHandler.class,
        JwtTokenService.class,
        TokenHeaderResolver.class,
        JwtTokenFilter.class,
        AccessRuleSet.class,
        AccessDecideInterceptor.class,
        LazydogJwtSecretConfig.class,
        SecurityConfig.class
})
@AutoConfiguration
public class SecurityAutoConfiguration {
}

