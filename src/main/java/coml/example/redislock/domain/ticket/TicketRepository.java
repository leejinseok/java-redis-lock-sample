package coml.example.redislock.domain.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select ticket from Ticket ticket where ticket.member is null and ticket.event.id = :eventId")
    List<Ticket> findAllByEventIdAndNotReserved(Long eventId);
}
