    package vn.edu.eiu.StudentMajorManagementSystem.module.student;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import vn.edu.eiu.StudentMajorManagementSystem.module.major.MajorStatsProjection;

    import java.util.List;

    // Lớp tiếp nhận và tiếp nhận xử lý các yêu cầu HTTP liên quan đến Sinh viên.
    // Định nghĩa các API endpoint thao tác trực tiếp với thông tin sinh viên.

    @RestController
    @RequestMapping("/api/students")
    public class StudentController {

        @Autowired
        private StudentService studentService;

        // lấy ds
        // Tích hợp lấy danh sách, phân trang, sắp xếp và tìm kiếm chung vào 1 API
        @GetMapping
        public ResponseEntity<Page<Student>> getAllStudents(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {

            // Sắp xếp mặc định theo ID tăng dần
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

            return ResponseEntity.ok(studentService.getAllStudents( pageable));
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
        public ResponseEntity<Page<Student>> searchStudents(@RequestParam(value = "name") String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            return ResponseEntity.ok(studentService.searchStudentsByName(name, pageable));
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
        // API tải file CSV dssv về máy
        @GetMapping("/export")
        public org.springframework.http.ResponseEntity<byte[]> exportStudents() {
            String csvData = studentService.exportStudentsToCsv();

            // Chuyển chuỗi chữ thành mảng byte với định dạng UTF-8 để tránh lỗi phông tiếng Việt
            byte[] output = csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            return org.springframework.http.ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_report.csv").contentType(org.springframework.http.MediaType.parseMediaType("text/csv")).body(output);
        }
        // API upload ảnh đại diện bằng phương thức POST dạng Form-Data
        @PostMapping("/{id}/avatar")
        public ResponseEntity<Student> uploadAvatar(
                @PathVariable Long id,
                @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
            return ResponseEntity.ok(studentService.uploadAvatar(id, file));
        }
    }