package vn.edu.eiu.StudentMajorManagementSystem.module.student;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.eiu.StudentMajorManagementSystem.module.major.Major;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100) //chỉ 1 email
    private String email;

    @Column(nullable = false)
    private Boolean gender;

    @ManyToOne // tạo khóa ngoại trong db
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;
    private String avatarUrl;
}