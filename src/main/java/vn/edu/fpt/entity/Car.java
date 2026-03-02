package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.ultis.enums.AccountRole;
import vn.edu.fpt.ultis.enums.CarStatus;
import vn.edu.fpt.ultis.enums.CarType;

import java.util.UUID;

@Entity
@Table(
        name = "car",
        indexes = {
                @Index(name = "idx_car_branch", columnList = "branch_id"),
                @Index(name = "idx_car_license_plate", columnList = "license_plate")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 50)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false, length = 50)
    private CarType carType;

    @Column(name = "total_seat", nullable = false)
    private Integer totalSeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CarStatus status;// "Running" or "Stop"

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}