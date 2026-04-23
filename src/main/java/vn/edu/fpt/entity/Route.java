package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "route")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Mã Route

    @Column(nullable = false, length = 100)
    private String name;  // Tên Route

    @Column(nullable = false)
    private boolean isActive;  // Trạng thái của Route

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteStation> routeStations;  // Danh sách các Station trong Route

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_revert_id")
    private Route routeRevert;
}