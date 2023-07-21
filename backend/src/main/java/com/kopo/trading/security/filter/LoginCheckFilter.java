package com.kopo.trading.security.filter;

import com.kopo.trading.security.JWTUtil;
import com.kopo.trading.security.exception.InvalidTokenException;
import com.kopo.trading.security.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.ZonedDateTime;

public class LoginCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Autowired
    public LoginCheckFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!checkURI(request.getRequestURI())) {
            String token = request.getHeader("Authorization").substring(7);
            if (!jwtUtil.validToken(token)) {
                throw new InvalidTokenException();
            } else if (jwtUtil.getExp(token).before(Date.from(ZonedDateTime.now().toInstant()))) {
                throw new TokenExpiredException();
            }
        }

        filterChain.doFilter(request, response);
    }

    // 여기가 화이트 리스트
    // 여기에 URL 등록하면 로그인 토큰 무시함
    private Boolean checkURI(String requestURI) {
        if (requestURI.startsWith("/")) {
            return true;
        }

        return false;
    }
}
