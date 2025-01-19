package com.stalker.cafemanagement.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerDetailsService customerDetailsService;


    Claims claims = null;
    private String username = null;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
            if(httpServletRequest.getServletPath().matches("/users/sign_in|/users/sign_up|users/forgot_password")){
                //no token required
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }else{
                String authorizationHeader = httpServletRequest.getHeader("Authorization");
                String token = null;
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                    claims = jwtUtil.extractAllClaims(token);
                    username = jwtUtil.extractUsername(token);
                }

                if (username != null && claims != null && jwtUtil.validateToken(token, username)) {
                    UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
                    if(jwtUtil.validateToken(token, userDetails.getUsername())){
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails,
                                        null,
                                        userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
    }

    public  boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public  boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser(){
        return username;
    }

}
