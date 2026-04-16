package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "seat",
        indexes = {
                @Index(name = "idx_seat_number", columnList = "seat_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "seat_number", nullable = false, unique = true, length = 10)
    private String seatNumber;
}