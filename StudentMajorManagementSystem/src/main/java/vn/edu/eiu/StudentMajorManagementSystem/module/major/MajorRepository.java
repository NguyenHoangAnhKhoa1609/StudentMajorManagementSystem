package vn.edu.eiu.StudentMajorManagementSystem.module.major;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
}