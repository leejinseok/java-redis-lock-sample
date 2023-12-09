package coml.example.redislock;

import coml.example.redislock.domain.event.Event;
import coml.example.redislock.domain.event.EventRepository;
import coml.example.redislock.domain.member.Member;
import coml.example.redislock.domain.member.MemberRepository;
import coml.example.redislock.domain.ticket.Ticket;
import coml.example.redislock.domain.ticket.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationCommandLineRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Member member = Member.of("홍길동");
        memberRepository.save(member);

        Event event = Event.of(20);
        for (int i = 0; i < 20; i++) {
            event.addTicket(new Ticket());
        }

        eventRepository.save(event);

    }
}
