package vn.edu.eiu.StudentMajorManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

//gọi các thuộc tính
@Entity
@Table(name = "majors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ự động tăng id trong db
    private Long id;

    @Column(nullable = false, length = 100) // Bắt buộc nhập max 100
    private String name;

    @Column(length = 255) // max 255
    private String description;

}