package vn.edu.fpt.dto.response.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RouteStatisticResponse {

    private Long routeId;
    private String routeName;
    private Long totalTickets;
}