package vn.edu.fpt.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private Long totalTickets;
    private Long totalPlans;
    private BigDecimal totalRevenue;

    private List<TopRouteItem> topRoutes;

    @Getter
    @Builder
    public static class TopRouteItem {
        private Long routeId;
        private String routeName;
        private Long totalTickets;
    }
}