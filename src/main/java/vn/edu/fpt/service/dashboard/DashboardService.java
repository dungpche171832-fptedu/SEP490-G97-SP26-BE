package vn.edu.fpt.service.dashboard;

import vn.edu.fpt.dto.response.DashboardResponse;

import java.time.LocalDateTime;

public interface DashboardService {

    DashboardResponse getDashboard(LocalDateTime start, LocalDateTime end);
}