package vn.edu.fpt.service.dashboard;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.edu.fpt.dto.projection.*;
import vn.edu.fpt.dto.response.DashboardResponse;
import vn.edu.fpt.repository.PlanRepository;
import vn.edu.fpt.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TicketRepository ticketRepository;
    private final PlanRepository planRepository;

    @Override
    public DashboardResponse getDashboard(LocalDateTime start, LocalDateTime end) {

        // ===== TICKET STATS =====
        TicketStatProjection ticketStats =
                ticketRepository.getTicketStats(start, end);

        // ===== PLAN STATS =====
        PlanStatProjection planStats =
                planRepository.getPlanStats(start, end);

        // ===== TOP ROUTES =====
        List<TopRouteProjection> routes =
                ticketRepository.getTopRoutes(
                        start,
                        end,
                        PageRequest.of(0, 4)
                );

        List<DashboardResponse.TopRouteItem> topRoutes =
                routes.stream()
                        .map(r -> DashboardResponse.TopRouteItem.builder()
                                .routeId(r.getRouteId())
                                .routeName(r.getRouteName())
                                .totalTickets(r.getTotalTickets())
                                .build())
                        .toList();

        return DashboardResponse.builder()
                .totalTickets(ticketStats.getTotalTickets())
                .totalRevenue(ticketStats.getTotalRevenue())
                .totalPlans(planStats.getTotalPlans())
                .topRoutes(topRoutes)
                .build();
    }
}