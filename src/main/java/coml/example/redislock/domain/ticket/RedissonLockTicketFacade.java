package coml.example.redislock.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockTicketFacade {

    private final RedissonClient redissonClient;

    private final TicketService ticketService;

    public void reserveTicket(Long eventId, Long memberId) {
        RLock lock = redissonClient.getLock("LOCK:EVENT:" + eventId.toString());
        try {
            boolean available = lock.tryLock(10, 3, TimeUnit.SECONDS);
            if (!available) {
                return;
            }

            ticketService.reserveTicket(eventId, memberId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


}
