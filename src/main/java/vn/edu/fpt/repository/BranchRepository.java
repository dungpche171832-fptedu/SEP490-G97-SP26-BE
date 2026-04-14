package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("""
        SELECT b FROM Branch b
        WHERE (:code IS NULL OR b.code LIKE %:code%)
          AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    List<Branch> filterBranches(@Param("code") String code,
                                @Param("name") String name);

    Optional<Branch> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}