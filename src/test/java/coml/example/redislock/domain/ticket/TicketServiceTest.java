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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static coml.example.redislock.BaseConfig.threadPoolExecutor;

@SpringBootTest
@ActiveProfiles({"test"})
class TicketServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketService ticketService;

    private final Logger logger = LogManager.getLogger(TicketServiceTest.class);

    public static final int EVENT_LIMIT = 1000;
    public static final int NUMBER_OF_REQUESTS = 1200;

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
    void 멀티쓰레드에서_티켓동시예매시_티켓개수보다_더많이_예매에_성공하는_테스트() throws InterruptedException {
        long beforeTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor();

        CountDownLatch latch = new CountDownLatch(NUMBER_OF_REQUESTS);
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            threadPoolExecutor.execute(() -> {
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
        logger.info("successCount: {}", successCount);

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);
        System.out.println("시간차이(m) : " + secDiffTime);

        Assertions.assertTrue(successCount.intValue() > EVENT_LIMIT);
    }

    @Test
    void 멀티쓰레드에서_티켓동시예매시_비관적락사용_테스트() throws InterruptedException {
        long beforeTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor();

        CountDownLatch latch = new CountDownLatch(NUMBER_OF_REQUESTS);
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            threadPoolExecutor.execute(() -> {
                try {
                    ticketService.reserveTicketInPessimisticLock(1L, 1L);
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
        logger.info("successCount: {}", successCount);

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);
        System.out.println("시간차이(m) : " + secDiffTime);

        Assertions.assertTrue(successCount.intValue() > EVENT_LIMIT);
    }




}