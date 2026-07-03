package vn.edu.eiu.StudentMajorManagementSystem.module.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.eiu.StudentMajorManagementSystem.module.major.Major;
import vn.edu.eiu.StudentMajorManagementSystem.module.major.MajorRepository;
import vn.edu.eiu.StudentMajorManagementSystem.module.major.MajorStatsProjection;

import java.util.List;


@Service
public class StudentService {

    @Autowired // Dependency Injection
    private StudentRepository studentRepository;
    @Autowired
    private MajorRepository majorRepository;

    // Tạo mới
    public Student createStudent(Student student) {
        if (student.getMajor() == null || student.getMajor().getId() == null) {
            throw new IllegalArgumentException("Sinh viên mới cần có Major");
        }

        Long majorId = student.getMajor().getId();

        Major major = majorRepository.findById(majorId).orElseThrow(() -> new RuntimeException("Major có ID " + majorId + " k tồn tại"));

        student.setMajor(major);
        return studentRepository.save(student);
    }

    // Lấy ds sv hiện có
    public Page<Student> getAllStudents(Pageable pageable) {

        return studentRepository.findAll(pageable);
    }

    // Lấy thông tin 1 sv qua ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }


    //  Cập nhật thông tin
    public Student updateStudent(Long id, Student studentUpdates) {
        // check sv cần được update có exist chưa
        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("notfound: " + id));

        // update
        existingStudent.setFullName(studentUpdates.getFullName());
        existingStudent.setEmail(studentUpdates.getEmail());
        existingStudent.setGender(studentUpdates.getGender());

        // xử lí major khi update
        if (studentUpdates.getMajor() != null && studentUpdates.getMajor().getId() != null) {
            Long newMajorId = studentUpdates.getMajor().getId();

            Major newMajor = majorRepository.findById(newMajorId).orElseThrow(() -> new RuntimeException("Major mới có ID " + newMajorId + " k tồn tại"));

            existingStudent.setMajor(newMajor);
        }

        return studentRepository.save(existingStudent);
    }

    // Xóa 1 sv theo mã ID
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    // tìm kiếm sinh viên theo tên có phân trang
    public Page<Student> searchStudentsByName(String name, Pageable pageable) {
        return studentRepository.findByFullNameContainingIgnoreCase(name, pageable);
    }

    //Lọc ds sv theo mã ngành
    public List<Student> getStudentsByMajor(Long majorId) {
        return studentRepository.findByMajorId(majorId);
    }

    // Thống kê mỗi ngành có bao nhiu sv
    public List<MajorStatsProjection> getMajorStats() {
        return studentRepository.getMajorStatistics();
    }

    // Xuất dssv thành chuỗi CSV dữ liệu
    public String exportStudentsToCsv() {
        List<Student> students = studentRepository.findAll();
        StringBuilder csvContent = new StringBuilder();

        // Viết dòng tiêu đề cột
        csvContent.append("ID,Full Name,Email,Gender,Major\n");

        // Duyệt qua từng sv để đổ dữ liệu vào dòng tiếp theo
        for (Student s : students) {
            String majorName = (s.getMajor() != null) ? s.getMajor().getName() : "N/A";
            csvContent.append(s.getId()).append(",").append(s.getFullName()).append(",").append(s.getEmail()).append(",").append(s.getGender()).append(",").append(majorName).append("\n");
        }

        return csvContent.toString();
    }
    // Hàm xử lý upload ảnh đại diện cho sinh viên
    public Student uploadAvatar(Long id, org.springframework.web.multipart.MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        try {
            // Định nghĩa thư mục lưu file
            String uploadDir = "uploads/";
            java.io.File directory = new java.io.File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Tự động tạo thư mục nếu chưa có
            }

            // Tạo tên file độc nhất theo ID sinh viên để tránh bị trùng/đè ảnh cũ
            String fileName = id + "_" + file.getOriginalFilename();
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + fileName);

            //  Sao chép dữ liệu file ảnh lưu xuống thư mục ổ cứng
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            //  Lưu đường dẫn file vào Database để sau này Front-end lấy ra hiển thị
            student.setAvatarUrl("/" + uploadDir + fileName);
            return studentRepository.save(student);

        } catch (java.io.IOException e) {
            throw new RuntimeException("Lỗi trong quá trình lưu file ảnh", e);
        }
    }

}