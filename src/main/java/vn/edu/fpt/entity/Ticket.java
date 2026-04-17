package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.ultis.enums.TicketStatus;

import java.math.BigDecimal;

@Entity
@Table(
        name = "ticket",
        indexes = {
                @Index(name = "idx_ticket_booking_code", columnList = "booking_code"),
                @Index(name = "idx_ticket_plan", columnList = "plan_id"),
                @Index(name = "idx_ticket_account", columnList = "account_id"),
                @Index(name = "idx_ticket_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "booking_code", nullable = false, unique = true, length = 50)
    private String bookingCode;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_station_id")
    private Station endStation;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    // ===== BUSINESS FIELDS =====

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    // ===== OPTIONAL (if needed) =====
    @Column(name = "note", length = 255)
    private String note;
}