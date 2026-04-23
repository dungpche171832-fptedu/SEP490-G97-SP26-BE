package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "route_station",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_route_station_route_station", columnNames = {"route_id", "station_id"}),
                @UniqueConstraint(name = "uk_route_station_route_order", columnNames = {"route_id", "station_order"})
        },
        indexes = {
                @Index(name = "idx_route_station_route", columnList = "route_id"),
                @Index(name = "idx_route_station_station", columnList = "station_id"),
                @Index(name = "idx_route_station_order", columnList = "station_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "station_order", nullable = false)
    private Integer stationOrder;
}