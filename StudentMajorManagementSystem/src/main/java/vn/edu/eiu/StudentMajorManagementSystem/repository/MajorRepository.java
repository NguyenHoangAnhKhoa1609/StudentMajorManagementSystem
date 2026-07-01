package vn.edu.eiu.StudentMajorManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.eiu.StudentMajorManagementSystem.Entity.Major;




@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
}