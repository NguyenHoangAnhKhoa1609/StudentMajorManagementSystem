package vn.edu.eiu.StudentMajorManagementSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.eiu.StudentMajorManagementSystem.Entity.Student;
import vn.edu.eiu.StudentMajorManagementSystem.repository.MajorStatsProjection;
import vn.edu.eiu.StudentMajorManagementSystem.service.StudentService;

import java.util.List;

// Lớp tiếp nhận và tiếp nhận xử lý các yêu cầu HTTP liên quan đến Sinh viên.
// Định nghĩa các API endpoint thao tác trực tiếp với thông tin sinh viên.

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // lấy ds
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // xem info chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    // add
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    // sửa info
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(updatedStudent);
    }

    // xóa sv
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentService.getStudentById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    // Tìm kiếm theo tên
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(studentService.searchStudentsByName(name));
    }

    // Lọc theo major
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> filterStudentsByMajor(@RequestParam(value = "majorId") Long majorId) {
        return ResponseEntity.ok(studentService.getStudentsByMajor(majorId));
    }

    // thống kê số sv major
    @GetMapping("/dashboard/major-stats")
    public ResponseEntity<List<MajorStatsProjection>> getMajorStats() {
        return ResponseEntity.ok(studentService.getMajorStats());
    }
}