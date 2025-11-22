package road_friend.road_friend_server.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 서버 내부 비밀키
    private final long expireMs = 1000 * 60 * 60 * 12; // 12시간

    // 토큰 생성 (role 포함)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // 역할 추가
                .setExpiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(key)
                .compact();
    }

    // 이메일 꺼내기
    public String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // role 꺼내기
    public String getRoleFromToken(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Claims 전체 추출 (필수)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}