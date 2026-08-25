package my.restproto.common.security.config;

import lombok.RequiredArgsConstructor;
import my.restproto.common.security.core.AccessDecideInterceptor;
import my.restproto.common.security.core.JwtTokenFilter;
import my.restproto.common.security.exception.DeniedHandler;
import my.restproto.common.security.exception.UnauthHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全链配置: 无状态无会话无跨域, URL 授权下放至 MVC 拦截器
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /** 未认证入口 */
    private final UnauthHandler unauthHandler;

    /** 权限拒绝处理器 */
    private final DeniedHandler deniedHandler;

    /** 令牌过滤器 */
    private final JwtTokenFilter jwtTokenFilter;

    /** 无状态安全链, 令牌过滤器前置于用户名密码过滤器 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthHandler)
                        .accessDeniedHandler(deniedHandler))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** 令牌过滤器注册项, 置为禁用避免 servlet 容器在安全链之外重复注册一遍 */
    @Bean
    public FilterRegistrationBean<JwtTokenFilter> tokenFilterRegistrationBean() {
        FilterRegistrationBean<JwtTokenFilter> filterRegistrationBean = new FilterRegistrationBean<>(jwtTokenFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Bean
    public WebMvcConfigurer authorizationConfig(AccessDecideInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor);
            }
        };
    }
}
