package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_driver_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDriverHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "old_driver_id", nullable = false)
    private Long oldDriverId;

    @Column(name = "new_driver_id", nullable = false)
    private Long newDriverId;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}