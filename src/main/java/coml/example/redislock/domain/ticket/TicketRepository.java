package coml.example.redislock.domain.ticket;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select ticket from Ticket ticket where ticket.member is null and ticket.event.id = :eventId")
    List<Ticket> findAllByEventIdAndNotReserved(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ticket from Ticket ticket where ticket.member is null and ticket.event.id = :eventId limit 1")
    Optional<Ticket> findAllByEventIdAndNotReservedWithPessimisticWrite(Long eventId);

}
