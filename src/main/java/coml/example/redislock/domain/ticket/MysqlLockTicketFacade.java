package coml.example.redislock.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MysqlLockTicketFacade {


    private final TicketLockRepository ticketLockRepository;
    private final TicketService ticketService;

    public void reserveTicket(long eventId, long memberId) {
        String key = "LOCK:EVENT:" + eventId;
        try {
            ticketLockRepository.getLock(key);
            ticketService.reserveTicket(eventId, memberId);
        } finally {
            ticketLockRepository.releaseLock(key);
        }
    }


}
