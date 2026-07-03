package vn.edu.eiu.StudentMajorManagementSystem.module.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    // Thời gian khóa tài khoản: 15 phút (900.000 mili giây)
    private static final long LOCK_TIME_DURATION = 900000;

    // 1. API Đăng ký tài khoản mới (Mật khẩu gửi lên sẽ được băm bằng BCrypt)
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request) {
        if (userRepository.findByUsername(request.get("username")).isPresent()) {
            return "Username đã tồn tại ";
        }

        User user = new User();
        user.setUsername(request.get("username"));
        // Băm mật khẩu trước khi lưu xuống DB để đảm bảo bảo mật
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setEmail(request.get("email"));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "Register successfully";
    }

    //  API Đăng nhập để lấy Token JWT
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        User user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        // Kiểm tra xem tk có đang bị khóa không
        if (user.getLockTime() != null) {
            long lockExpireTime = user.getLockTime().getTime() + LOCK_TIME_DURATION;
            if (System.currentTimeMillis() < lockExpireTime) {
                long minutesLeft = ((lockExpireTime - System.currentTimeMillis()) / 1000) / 60;
                throw new RuntimeException("Tài khoản đang bị khóa! Vui lòng thử lại sau " + minutesLeft + " phút.");
            } else {
                // Đã quá 15 phút khóa -> Tự động mở khóa để cho đăng nhập tiếp
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user);
            }
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            // Sai mật khẩu -> Tăng số lần sai lên 1
            int newAttempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(newAttempts);

            if (newAttempts >= 5) {
                // Sai quá 5 lần -> Tiến hành khóa tài khoản
                user.setLockTime(new java.util.Date());
                userRepository.save(user);
                throw new RuntimeException("Bạn đã nhập sai mật khẩu 5 lần. Tài khoản bị khóa 15 phút!");
            }

            userRepository.save(user);
            throw new RuntimeException("Sai mật khẩu! Bạn còn " + (5 - newAttempts) + " lần thử.");
        }

        //  Đăng nhập thành công -> Reset lại số lần sai về 0
        user.setFailedAttempt(0);
        user.setLockTime(null);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return Map.of("accessToken", token, "tokenType", "Bearer");
    }
    //  API lấy thông tin tk hiện tại từ Token JWT
    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        // Lấy cái tên username từ  request JwtAuthenticationFilter đã làm
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Tìm trong DB và trả về thông tin sạch
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
    //Đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@RequestBody Map<String, String> request) {
        // Lấy username người đang đăng nhập từ SecurityContext
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        // 2. Kiểm tra xem mật khẩu cũ truyền lên có khớp với mật khẩu đang lưu dưới DB k
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Wrong old password");
        }

        // Mã hóa băm mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Change password successfully";
    }
    // API Quên mật khẩu
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        // Tìm user dựa vào username gửi lên
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với username này"));
        // Tiến hành băm mật khẩu mới và đè lên mật khẩu cũ
        user.setPassword(passwordEncoder.encode(newPassword));
        // XÁC THỰC: Kiểm tra email truyền lên có khớp với email của tài khoản k
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Email xác thực không chính xác");
        }
        // Khớp hoàn toàn thì tiến hành đổi pass và reset số lần sai
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFailedAttempt(0);
        user.setLockTime(null);

        userRepository.save(user);

        return "Reset password successfully";
    }

}