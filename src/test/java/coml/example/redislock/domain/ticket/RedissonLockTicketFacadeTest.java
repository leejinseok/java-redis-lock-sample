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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class RedissonLockTicketFacadeTest {

    @Autowired
    private RedissonLockTicketFacade redissonLockTicketFacade;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    private final int EVENT_LIMIT = 20;

    @BeforeEach
    void setUp() {
        Member member = Member.of("홍길동");
        memberRepository.save(member);
        Event event = Event.of(EVENT_LIMIT);
        for (int i = 0; i < EVENT_LIMIT; i++) {
            event.addTicket(new Ticket());
        }
        eventRepository.save(event);
    }


    @Test
    void 멀티쓰레드에서_티켓동시예매시_레디스락을_활용하여_테스트() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger(0);
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    redissonLockTicketFacade.reserveTicket(1L, 1L);
                    int increment = successCount.incrementAndGet();
                    successCount.set(increment);
                } catch (RuntimeException e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Assertions.assertEquals(EVENT_LIMIT, successCount.intValue());
    }


}