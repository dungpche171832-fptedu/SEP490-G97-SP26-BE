package vn.edu.fpt.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityResponse {
    private long id;
    private String name;
}