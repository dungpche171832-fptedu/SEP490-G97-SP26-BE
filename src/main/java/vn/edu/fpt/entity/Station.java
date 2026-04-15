package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "station",
        indexes = {
                @Index(name = "idx_station_name", columnList = "name"),
                @Index(name = "idx_station_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;  // Tên điểm dừng

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;  // Mã điểm dừng (dùng khi đặt vé)

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;  // Vĩ độ

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;  // Kinh độ

    @Column(name = "address", length = 255)
    private String address;  // Địa chỉ chi tiết

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;  // ID tỉnh/thành
}