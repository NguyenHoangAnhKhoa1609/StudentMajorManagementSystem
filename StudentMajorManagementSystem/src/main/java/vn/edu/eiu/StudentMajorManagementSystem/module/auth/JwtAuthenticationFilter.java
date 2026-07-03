package vn.edu.eiu.StudentMajorManagementSystem.module.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy chuỗi JWT từ request gửi lên
            String jwt = getJwtFromRequest(request);

            // Nếu có token và token hợp lệ thì tiến hành đọc thông tin
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                //  Lấy thông tin User từ DB để xem người này có Role gì
                userRepository.findByUsername(username).ifPresent(user -> {
                    // Chuyển chuỗi role từ DB  thành đối tượng quyền hạn của Spring
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of(authority));

                    //  Hợp lệ hoàn toàn thì  cho phép request vượt qua trạm gác
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        } catch (Exception ex) {
            // Có lỗi xảy ra khi xác thực thì log ra hoặc bỏ qua để Spring Security tự chặn
            logger.error("Không thể xác thực người dùng", ex);
        }

        // Chuyển tiếp request sang bước xử lý tiếp theo
        filterChain.doFilter(request, response);
    }

    // Hàm phụ trợ cắt lấy chuỗi Token từ Header "Authorization: Bearer <chuỗi_jwt>"
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}