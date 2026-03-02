package vn.edu.fpt.repository;

import vn.edu.fpt.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findAll();  // Tìm tất cả chi nhánh
}