package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Các phương thức tuỳ chỉnh khác nếu cần
}