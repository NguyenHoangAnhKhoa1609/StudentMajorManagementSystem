package vn.edu.eiu.StudentMajorManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.eiu.StudentMajorManagementSystem.Entity.Student;
import org.springframework.data.jpa.repository.Query;
import java.util.*;

// extend JpaRepository có sẵn các hàm CRUD (findAll, findById, save, deleteById)
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Kiểu dữ liệu quản lý là Student, Kiểu dữ liệu của khóa chính (ID) là Long
    // 1. Tìm kiếm sv theo tên
    List<Student> findByFullNameContainingIgnoreCase(String name);

    // 2. Lọc sinh viên theo ID major
    List<Student> findByMajorId(Long majorId);
    @Query("SELECT s.major.name AS majorName, COUNT(s) AS studentCount " +"FROM Student s " + "GROUP BY s.major.name")
    List<MajorStatsProjection> getMajorStatistics();
}
