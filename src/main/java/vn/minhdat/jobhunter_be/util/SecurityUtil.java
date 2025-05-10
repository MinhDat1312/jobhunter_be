package vn.minhdat.jobhunter_be.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${minhdat.jwt.base64-secret}")
    private String jwtKey;
    @Value("${minhdat.jwt.access-token-validity-in-seconds}")
    private long jwtAccessToken;
    @Value("${minhdat.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshToken;
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(Authentication authentication){
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessToken, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("user", authentication)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String createRefreshToken(String email, LoginResponse loginResponse){
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtRefreshToken, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", loginResponse.getUserLogin())
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(extractPrincipal(authentication));
    }

    private static String extractPrincipal(Authentication authentication){
        if(authentication == null){
            return null;
        } else if(authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if(authentication.getPrincipal() instanceof Jwt jwt){
            return jwt.getSubject();
        } else if(authentication.getPrincipal() instanceof String s){
            return s;
        }
        return null;
    }
}
