package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.entity.News;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("""
        SELECT n FROM News n
        WHERE n.isDeleted = false
        AND n.isActive = true
        AND (n.startTime IS NULL OR n.startTime <= :now)
        AND (n.endTime IS NULL OR n.endTime >= :now)
        ORDER BY n.displayOrder ASC, n.createdAt DESC
    """)
    List<News> findActiveNews(LocalDateTime now);

    List<News> findByIsDeletedFalse();
}