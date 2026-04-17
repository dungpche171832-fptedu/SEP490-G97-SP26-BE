package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(
        name = "branch",
        indexes = {
                @Index(name = "idx_branch_code", columnList = "code"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Mã chi nhánh

    @Column(nullable = false, length = 100)
    private String name;  // Tên chi nhánh

    @Column(length = 255)
    private String address;  // Địa chỉ chi nhánh

    @Column(length = 20)
    private String phone;  // Số điện thoại

    @Column(length = 100)
    private String email;  // Email chi nhánh

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // Trạng thái hoạt động

    // Trường để lưu đường dẫn ảnh
    @Column(name = "image_url", length = 255)
    private String imageUrl;  // Đường dẫn tới ảnh chi nhánh
}