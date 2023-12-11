package coml.example.redislock.domain.ticket;

import coml.example.redislock.domain.member.Member;
import coml.example.redislock.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final RedissonClient redissonClient;
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean reserveTicketWithLock(Long eventId, Long memberId) {
        RLock lock = redissonClient.getLock("LOCK:EVENT:" + eventId.toString());

        String name = Thread.currentThread().getName();

        try {
            boolean available = lock.tryLock(10, 3, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("Lock을 획득하지 못했습니다.");
            }

            log.info("TicketService > threadName : {} start", name);
            reserveTicket(eventId, memberId);
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                log.info("TicketService > threadName : {} shutdown", name);
                lock.unlock();
            } catch (Exception e) {
                log.error("TicketService > reserveTicketWithLock > unlock exception ", e);
            }
        }
    }

    @Transactional
    public void reserveTicket(Long eventId, Long memberId) {
        List<Ticket> allByNotReserved = ticketRepository.findAllByEventIdAndNotReserved(eventId);
        Optional<Ticket> first = allByNotReserved.stream().findFirst();
        if (first.isEmpty()) {
            throw new RuntimeException("더이상 예약가능한 티켓이 존재하지 않습니다.");
        }

        Ticket ticket = first.get();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        ticket.reservedBy(member);
    }


}
