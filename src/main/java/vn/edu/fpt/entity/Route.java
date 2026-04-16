package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "route",
        indexes = {
                @Index(name = "idx_route_name", columnList = "name"),
                @Index(name = "idx_route_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name; // Tên tuyến

    @Column(name = "start_route", nullable = false, length = 150)
    private String startRoute; // Chi nhánh xuất phát

    @Column(name = "end_route", nullable = false, length = 150)
    private String endRoute; // Chi nhánh kết thúc

    @Column(name = "description", nullable = false, length = 255)
    private String description; // Mô tả tuyến

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}