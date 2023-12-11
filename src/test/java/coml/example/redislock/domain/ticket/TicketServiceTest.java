package coml.example.redislock.domain.ticket;

import coml.example.redislock.domain.event.Event;
import coml.example.redislock.domain.event.EventRepository;
import coml.example.redislock.domain.member.Member;
import coml.example.redislock.domain.member.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public static final Logger logger = LogManager.getLogger(TicketServiceTest.class);

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

    @Test
    void 멀티쓰레드에서_티켓동시예매시_레디스락을_활용하여_테스트() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger(0);
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    boolean b = ticketService.reserveTicketWithLock(1L, 1L);
                    if (b) {
                        int increment = successCount.incrementAndGet();
                        successCount.set(increment);
                    }
                } catch (RuntimeException e) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Assertions.assertEquals(EVENT_LIMIT, successCount.intValue());
    }


}