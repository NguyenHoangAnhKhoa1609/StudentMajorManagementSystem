package vn.edu.eiu.StudentMajorManagementSystem.module.major;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;

    // lấy ds major
    @GetMapping
    public List<Major> getAllMajors() {
        return majorService.getAllMajors();
    }

    // lấy info 1 major theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Major> getMajorById(@PathVariable Long id) {
        Major major = majorService.getMajorById(id);
        return major != null ? ResponseEntity.ok(major) : ResponseEntity.notFound().build();
    }

    // Tạo mới (Đã đồng bộ)
    @PostMapping
    public ResponseEntity<Major> createMajor(@RequestBody Major major) {
        return ResponseEntity.ok(majorService.createMajor(major));
    }

    // Cập nhật info theo ID
    @PutMapping("/{id}")
    public ResponseEntity<Major> updateMajor(@PathVariable Long id, @RequestBody Major majorDetails) {
        Major updatedMajor = majorService.updateMajor(id, majorDetails);
        return ResponseEntity.ok(updatedMajor);
    }

    // Xóa theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseEntity.ok().build();
    }
}