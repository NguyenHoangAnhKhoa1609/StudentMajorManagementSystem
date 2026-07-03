package vn.edu.eiu.StudentMajorManagementSystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.eiu.StudentMajorManagementSystem.module.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF làm API với Token JWT
                .authorizeHttpRequests(auth -> auth
                        // 1. Các API Đăng ký, Đăng nhập, Quên mật khẩu mở tự do
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/forgot-password").permitAll()

                        // 2. API Upload Avatar (Cả USER và ADMIN đều được phép)
                        .requestMatchers(HttpMethod.POST, "/api/students/*/avatar").hasAnyAuthority("USER", "ADMIN")

                        // 3. Quyền GET: Xem danh sách công khai (Cả ADMIN và USER)
                        .requestMatchers(HttpMethod.GET, "/api/students", "/api/students/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/majors", "/api/majors/**").hasAnyAuthority("USER", "ADMIN")

                        // 4. Quyền POST, PUT, DELETE: Thao tác dữ liệu (Chỉ ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/students", "/api/students/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/majors", "/api/majors/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/students/**", "/api/majors/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**", "/api/majors/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}