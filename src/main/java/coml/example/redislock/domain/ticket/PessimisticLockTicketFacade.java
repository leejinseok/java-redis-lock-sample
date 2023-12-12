package coml.example.redislock.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PessimisticLockTicketFacade {

    private final TicketService ticketService;

    public void reserveTicket(long eventId, long memberId) {
        ticketService.reserveTicketInPessimisticLock(eventId, memberId);
    }
}
