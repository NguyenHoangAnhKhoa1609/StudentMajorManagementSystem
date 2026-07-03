package vn.edu.eiu.StudentMajorManagementSystem.module.major;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.eiu.StudentMajorManagementSystem.module.student.StudentRepository; // Nhúng thêm thằng này để check sinh viên

import java.util.List;

@Service
public class MajorService {

    @Autowired // Dependency Injection
    private MajorRepository majorRepository;

    @Autowired // Nhúng thêm để đếm số lượng sinh viên của ngành
    private StudentRepository studentRepository;

    // Lấy all ds từ db
    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    // Tìm kiếm 1 major theo ID trả về null nếu không tìm thấy
    public Major getMajorById(Long id) {
        return majorRepository.findById(id).orElse(null);
    }

    //  Tạo mới
    public Major createMajor(Major major) {
        return majorRepository.save(major);
    }

    //  Cập nhật  major
    public Major updateMajor(Long id, Major majorUpdates) {
        Major existingMajor = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notfound major có ID: " + id));

        existingMajor.setName(majorUpdates.getName());
        existingMajor.setDescription(majorUpdates.getDescription());

        return majorRepository.save(existingMajor);
    }

    //  Xóa
    public void deleteMajor(Long id) {
        //  Check xem ngành này có tồn tại k
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên ngành có ID: " + id));

        //  Check có sv nào học ngành này
        int studentCount = studentRepository.findByMajorId(id).size();

        // Check ko xóa ngành đang có sv học
        if (studentCount > 0) {
            throw new RuntimeException("E là không thể, đang có  " + studentCount + " sv học");
        }

        majorRepository.deleteById(id);
    }
}