package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;

@Entity
@Table(
        name = "plan_seat",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_plan_seat_plan_seat", columnNames = {"plan_id", "seat_id"})
        },
        indexes = {
                @Index(name = "idx_plan_seat_plan", columnList = "plan_id"),
                @Index(name = "idx_plan_seat_seat", columnList = "seat_id"),
                @Index(name = "idx_plan_seat_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PlanSeatStatus status;

    // Thêm trường ticket với khả năng nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = true)  // nullable = true để trường này có thể rỗng
    private Ticket ticket;
}