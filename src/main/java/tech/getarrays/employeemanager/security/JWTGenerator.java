package tech.getarrays.employeemanager.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        // Collect the user's roles (authorities)
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Get role names (e.g., "ROLE_USER")
                .collect(Collectors.joining(","));

        // Create the JWT with roles included
        String token = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)  // Add roles to the JWT payload
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                .compact();

        return token;
    }

    public String getUsernameFromJWT(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public List<String> getRolesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String roles = claims.get("roles", String.class);
        return Arrays.asList(roles.split(","));  // Split roles into a list
    }
    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(SecurityConstants.JWT_SECRET)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect", ex);
        }
    }
}
