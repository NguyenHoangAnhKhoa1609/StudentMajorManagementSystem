package vn.edu.eiu.StudentMajorManagementSystem.module.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
    @Column(name = "failed_attempt", nullable = false)
    private int failedAttempt = 0;

    @Column(name = "lock_time")
    private java.util.Date lockTime;
    @Column(unique = true, nullable = false)
    private String email;
}