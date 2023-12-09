package coml.example.redislock.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final RedissonClient redissonClient;

    public void reserveTicketWithLock(Long eventId, Long memberId) {
        String lockKey = "event:" + eventId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("Lock을 획득하지 못했습니다.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void reverseTicket(Long eventId, Long memberId) {

    }


}
