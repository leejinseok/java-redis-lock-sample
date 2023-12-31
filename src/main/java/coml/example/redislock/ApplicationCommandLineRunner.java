package coml.example.redislock;

import coml.example.redislock.domain.event.EventRepository;
import coml.example.redislock.domain.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationCommandLineRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
//        Member member = Member.of("홍길동");
//        memberRepository.save(member);
//
//        Event event = Event.of(20);
//        for (int i = 0; i < 20; i++) {
//            event.addTicket(new Ticket());
//        }
//
//        eventRepository.save(event);
    }

}
