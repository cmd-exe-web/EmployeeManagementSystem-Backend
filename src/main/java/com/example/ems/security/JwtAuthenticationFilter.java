package com.example.ems.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. get token
        String requestToken = request.getHeader("Authorization");

        //Bearer 23424234
        System.out.println(requestToken);

        String username = null;

        String token = null;

        if(requestToken != null && requestToken.startsWith("Bearer")){

            token = requestToken.substring(7);

            try {
                username = jwtTokenHelper.getUsernameFromToken(token);
            }
            catch (IllegalArgumentException e){
                System.out.println("Unable to get Jwt token");
            }
            catch (ExpiredJwtException e){
                System.out.println("Jwt token has expired");
            }
            catch (MalformedJwtException e){
                System.out.println("Invalid jwt");
            }


        }else {
            System.out.println("JWT token doesn't begin with Bearer or is null!!");
        }

        //once we get the token, now validate

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //check if jwt token matches user details
            if(jwtTokenHelper.validateToken(token, userDetails)){
                //everything is correct, token is valid
                //now we have to authenticate

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else {
                System.out.println("Invalid jwt token(jwt token doesn't match the user");
            }
        }
        else {
            System.out.println("Username is null or context is not null");

        }

        filterChain.doFilter(request, response);
    }
}
