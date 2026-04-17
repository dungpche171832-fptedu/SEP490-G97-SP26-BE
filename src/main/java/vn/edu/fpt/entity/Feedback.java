package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "feedback",
        indexes = {
                @Index(name = "idx_feedback_ticket", columnList = "ticket_id"),
                @Index(name = "idx_feedback_account", columnList = "account_id"),
                @Index(name = "idx_feedback_rating", columnList = "rating")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1 - 5 sao

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;
}