package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.ultis.enums.CarType;

import java.math.BigDecimal;

@Entity
@Table(
        name = "rule",
        indexes = {
                @Index(name = "idx_rule_car_type", columnList = "car_type"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false, length = 50)
    private CarType carType;

    @Column(name = "min_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal minKm; // cận dưới, inclusive

    @Column(name = "max_km", precision = 10, scale = 2)
    private BigDecimal maxKm; // cận trên, exclusive; null = không giới hạn trên

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
}