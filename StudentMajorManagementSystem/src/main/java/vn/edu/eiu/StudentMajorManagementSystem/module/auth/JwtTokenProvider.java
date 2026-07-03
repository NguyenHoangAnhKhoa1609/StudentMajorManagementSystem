package vn.edu.eiu.StudentMajorManagementSystem.module.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Tạo một mã bí mật ngẫu nhiên đủ độ dài bảo mật để ký và mã hóa Token
    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Thời gian hết hạn của token (1 ngày = 86400000 mili giây)
    private final long jwtExpirationInMs = 86400000;

    // 1. Hàm tạo chuỗi Token JWT từ username
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)    // Ghi tên người sở hữu vào thẻ
                .setIssuedAt(now)         // Ghi ngày phát hành thẻ (Bây giờ)
                .setExpiration(expiryDate)// Ghi ngày thẻ hết hạn
                .signWith(jwtSecret)      // Đóng cái dấu chìm bảo mật của Server vào đây
                .compact();               // Nén tất cả lại thành một chuỗi ký tự loằng ngoằng
    }

    // 2. Hàm giải mã để lấy lại username từ chuỗi Token JWT
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret) // Đưa chìa khóa bí mật vào máy quét
                .build()
                .parseClaimsJws(token)    // Bỏ cái chuỗi Token của người dùng vào để giải mã
                .getBody();               // Cắt lấy phần nội dung cốt lõi bên trong thẻ

        return claims.getSubject();       // Lấy ra cái tên "username" lúc nãy mình ghi vào thẻ
    }

    // 3. Hàm kiểm tra xem Token gửi lên có đúng do mình in ra và còn hạn không
    public boolean validateToken(String authToken) {
        try {
            // Đút token vào máy quét xem chữ ký (dấu chìm) có trùng khớp không và còn hạn không
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}