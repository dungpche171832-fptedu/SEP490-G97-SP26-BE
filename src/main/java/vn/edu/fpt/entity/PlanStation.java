package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "plan_station",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_plan_station_plan_station", columnNames = {"plan_id", "station_id"}),
                @UniqueConstraint(name = "uk_plan_station_plan_order", columnNames = {"plan_id", "station_order"})
        },
        indexes = {
                @Index(name = "idx_plan_station_plan", columnList = "plan_id"),
                @Index(name = "idx_plan_station_station", columnList = "station_id"),
                @Index(name = "idx_plan_station_order", columnList = "station_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "station_order", nullable = false)
    private Integer stationOrder;
}