package vn.edu.fpt.dto.response.station;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationListResponse {
    private List<StationResponse> stations;
    private String message;
    private int totalCount;
}