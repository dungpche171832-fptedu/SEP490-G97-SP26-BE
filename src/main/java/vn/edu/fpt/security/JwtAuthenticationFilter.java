package vn.edu.fpt.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import vn.edu.fpt.entity.Account;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.ultis.enums.AccountStatus;
import vn.edu.fpt.ultis.errorCode.AccountErrorCode;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    /**
     * BỎ QUA JWT FILTER cho:
     * - Auth API
     * - Swagger UI
     * - OpenAPI docs
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/api/plans");

    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtil.validateToken(jwt)) {

                Claims claims = jwtUtil.getClaims(jwt);

                String username = claims.getSubject();

                String role = claims.get("role", String.class);

                Integer tokenVersion =
                        claims.get("tokenVersion", Integer.class);

// GET ACCOUNT
                Account account = accountRepository
                        .findByEmail(username)
                        .orElse(null);

                if (account == null) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");

                    response.getWriter().write("""
    {
        "code": 401,
        "message": "Tài khoản không tồn tại"
    }
    """);

                    return;
                }

// CHECK STATUS
                // CHECK STATUS
                if (account.getStatus() == AccountStatus.INACTIVE) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");

                    response.getWriter().write("""
    {
        "code": 401,
        "message": "Tài khoản đã bị khóa"
    }
    """);

                    return;
                }

// CHECK TOKEN VERSION
                if (tokenVersion == null
                        || !tokenVersion.equals(account.getTokenVersion())) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");

                    response.getWriter().write("""
    {
        "code": 401,
        "message": "Phiên đăng nhập đã hết hạn"
    }
    """);

                    return;
                }

// ROLE_XXX là bắt buộc
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(authority)
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("JWT authentication failed", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth)
                && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
