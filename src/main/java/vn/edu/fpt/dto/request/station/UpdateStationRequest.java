package vn.edu.fpt.dto.response.station;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStationRequest {

    @NotBlank(message = "Tên điểm dừng không được để trống")
    @Size(max = 100, message = "Tên điểm dừng tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Mã điểm dừng không được để trống")
    @Size(max = 50, message = "Mã điểm dừng tối đa 50 ký tự")
    private String code;

    @NotNull(message = "Vĩ độ không được để trống")
    private BigDecimal latitude;

    @NotNull(message = "Kinh độ không được để trống")
    private BigDecimal longitude;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @NotNull(message = "City id không được để trống")
    private Long cityId;
}