package coml.example.redislock.domain.ticket;

import coml.example.redislock.domain.event.Event;
import coml.example.redislock.domain.event.EventRepository;
import coml.example.redislock.domain.member.Member;
import coml.example.redislock.domain.member.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles({"test"})
class TicketServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketService ticketService;

    private final int EVENT_LIMIT = 20;

    @BeforeEach
    void setUp() {
        Member member = Member.of("홍길동");
        memberRepository.save(member);
        Event event = Event.of(EVENT_LIMIT);
        for (int i = 0; i < 20; i++) {
            event.addTicket(new Ticket());
        }
        eventRepository.save(event);
    }

    @Test
    void 멀티쓰레드에서_티켓동시예매시_티켓개수보다_더많이_예매에_성공하는_테스트() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger(0);

        int numberOfThreads = 10000;
        ExecutorService service = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    ticketService.reserveTicket(1L, 1L);
                    int increment = successCount.incrementAndGet();
                    successCount.set(increment);

                } catch (RuntimeException ignored) {

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }

        latch.await();
        Assertions.assertTrue(successCount.intValue() > EVENT_LIMIT);
    }


}