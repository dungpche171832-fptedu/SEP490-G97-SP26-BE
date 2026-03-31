package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "role",
        indexes = {
                @Index(name = "idx_role_name", columnList = "name", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}