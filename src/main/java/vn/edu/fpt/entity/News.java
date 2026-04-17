package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "news",
        indexes = {
                @Index(name = "idx_news_title", columnList = "title"),
                @Index(name = "idx_news_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "status", nullable = false)
    private String status;
    // DRAFT / PUBLISHED / HIDDEN

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
}