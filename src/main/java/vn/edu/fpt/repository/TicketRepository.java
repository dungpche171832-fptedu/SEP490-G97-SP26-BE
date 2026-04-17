package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    // Các phương thức tuỳ chỉnh khác nếu cần
}