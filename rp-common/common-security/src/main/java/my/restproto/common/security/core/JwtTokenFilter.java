package my.restproto.common.security.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.restful.ResponseWriter;
import my.restproto.common.restful.CommonResult;
import my.restproto.common.security.core.jwt.JwtTokenService;
import my.restproto.common.security.core.jwt.model.JwtTokenClaims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 令牌过滤器, 从请求头按携带模板提取令牌, 校验签名与撤销状态后写入安全上下文
 */
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    /** 令牌服务 */
    private final JwtTokenService jwtTokenService;

    /** 令牌头解析器 */
    private final TokenHeaderResolver tokenHeaderResolver;

    /** 统一响应写入器 */
    private final ResponseWriter responseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 头缺失或不符携带模板均视为未携带令牌, 直接放行, 是否拦截交由授权层决定
        String token = tokenHeaderResolver.resolveToken(request);
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析令牌, 签名或过期失败输出 401
        JwtTokenClaims parsed;
        try {
            parsed = jwtTokenService.parse(token);
        } catch (BadCredentialsException ex) {
            unauth(response);
            return;
        }

        // 已撤销令牌输出 401
        if (jwtTokenService.isRevoked(parsed)) {
            unauth(response);
            return;
        }

        // 将身份与权限写入安全上下文, 认证对象携带令牌串供下游取用后放行
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                parsed.getStandard().getSubject(),
                null,
                parsed.getExtended().getPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());
        authentication.setDetails(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    /** 输出 401 统一响应 */
    private void unauth(HttpServletResponse response) throws IOException {
        responseWriter.write(
                response,
                ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(CommonResult.unauth()));
    }
}
