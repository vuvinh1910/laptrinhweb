package com.example.web_nhom_5.configuration;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TokenFilter implements Filter {

    private final JwtDecoder jwtDecoder;
    private final List<String> publicUrls = List.of("/public/**", "/css/**", "/js/**", "/img/**"); // Endpoint công khai
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public TokenFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();

        // Nếu yêu cầu là công khai, bỏ qua kiểm tra token
        if (isPublicEndpoint(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        // Lấy token từ cookie
        String token = getTokenFromCookie(httpRequest);

        if (StringUtils.hasText(token)) {
            try {
                // Giải mã JWT
                Jwt jwt = jwtDecoder.decode(token);
                Object scopeClaim = jwt.getClaims().get("scope");

                // Kiểm tra nếu "scope" là chuỗi hoặc danh sách
                if (scopeClaim != null) {
                    List<String> roles = new ArrayList<>();

                    // Kiểm tra xem "scope" có phải là chuỗi hay danh sách
                    if (scopeClaim instanceof String) {
                        String scope = (String) scopeClaim;
                        roles = List.of(scope.split(" ")); // Tách chuỗi thành danh sách vai trò
                    } else if (scopeClaim instanceof List) {
                        roles = (List<String>) scopeClaim;
                    }

                    // Kiểm tra quyền truy cập cho trang yêu cầu
                    if (requestPath.startsWith("/admin") && !roles.contains("ROLE_ADMIN")) {
                        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for this role");
                        return;
                    }

                    // Kiểm tra quyền truy cập cho trang yêu cầu ROLE_USER
                    if (requestPath.startsWith("/users") && !roles.contains("ROLE_USER")) {
                        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for this role");
                        return;
                    }

                    // Nếu quyền hợp lệ, xử lý vai trò
                    processRoles(roles, jwt);
                }

            } catch (JwtException e) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        for (String pattern : publicUrls) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void processRoles(List<String> roles, Jwt jwt) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.trim()))  // Thêm "ROLE_" nếu chưa có
                .collect(Collectors.toList());

        // Tạo đối tượng UserDetails và Authentication
        UserDetails userDetails = new User(jwt.getSubject(), "", authorities);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
