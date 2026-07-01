package vn.edu.eiu.StudentMajorManagementSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.eiu.StudentMajorManagementSystem.Entity.Major;
import vn.edu.eiu.StudentMajorManagementSystem.Entity.Student;
import vn.edu.eiu.StudentMajorManagementSystem.repository.MajorRepository;
import vn.edu.eiu.StudentMajorManagementSystem.repository.MajorStatsProjection;
import vn.edu.eiu.StudentMajorManagementSystem.repository.StudentRepository;

import java.util.List;
import java.util.Optional;


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

        Major major = majorRepository.findById(majorId) .orElseThrow(() -> new RuntimeException("Major có ID " + majorId + " k tồn tại"));

        student.setMajor(major);
        return studentRepository.save(student);
    }

    // Lấy ds sv hiện có
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
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

            Major newMajor = majorRepository.findById(newMajorId) .orElseThrow(() -> new RuntimeException("Major mới có ID " + newMajorId + " k tồn tại"));

            existingStudent.setMajor(newMajor);
        }

        return studentRepository.save(existingStudent);
    }

    // Xóa 1 sv theo mã ID
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name);
    }
    //Lọc ds sv theo mã ngành
    public List<Student> getStudentsByMajor(Long majorId) {
        return studentRepository.findByMajorId(majorId);
    }
    // Thống kê mỗi ngành có bao nhiu sv
    public List<MajorStatsProjection> getMajorStats() {
        return studentRepository.getMajorStatistics();
    }
}