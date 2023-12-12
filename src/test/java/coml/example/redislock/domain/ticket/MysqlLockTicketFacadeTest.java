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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static coml.example.redislock.BaseConfig.threadPoolExecutor;
import static coml.example.redislock.domain.ticket.TicketServiceTest.EVENT_LIMIT;
import static coml.example.redislock.domain.ticket.TicketServiceTest.NUMBER_OF_REQUESTS;

@SpringBootTest
@ActiveProfiles({"test"})
class MysqlLockTicketFacadeTest {

    @Autowired
    private MysqlLockTicketFacade mysqlLockTicketFacade;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

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
    void mysql락을_활용하여_티켓예매_테스트() throws InterruptedException {
        long beforeTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);

        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor();
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_REQUESTS);

        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            threadPoolExecutor.execute(() -> {
                try {
                    mysqlLockTicketFacade.reserveTicket(1L, 1L);
                    int increment = successCount.incrementAndGet();
                    successCount.set(increment);
                } catch (RuntimeException e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);
        System.out.println("시간차이(m) : " + secDiffTime);

        Assertions.assertEquals(EVENT_LIMIT, successCount.intValue());
    }

}