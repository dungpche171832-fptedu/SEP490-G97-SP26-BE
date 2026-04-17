package vn.edu.fpt.dto.response.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TicketListResponse {
    private List<TicketResponse> tickets;  // Danh sách các vé
    private String message;  // Thông báo trả về
    private int totalCount;  // Tổng số vé
}