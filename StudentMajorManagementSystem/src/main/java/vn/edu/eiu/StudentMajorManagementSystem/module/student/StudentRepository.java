package vn.edu.eiu.StudentMajorManagementSystem.module.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.eiu.StudentMajorManagementSystem.module.major.MajorStatsProjection;

import java.util.*;

// extend JpaRepository có sẵn các hàm CRUD (findAll, findById, save, deleteById)
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Kiểu dữ liệu quản lý là Student, Kiểu dữ liệu của khóa chính (ID) là Long
    // 1. Tìm kiếm sv theo tên
    // phân trang tìm kiếm
    Page<Student> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    // 2. Lọc sinh viên theo ID major
    List<Student> findByMajorId(Long majorId);
    @Query("SELECT s.major.name AS majorName, COUNT(s) AS studentCount " +"FROM Student s " + "GROUP BY s.major.name")
    List<MajorStatsProjection> getMajorStatistics();
}
