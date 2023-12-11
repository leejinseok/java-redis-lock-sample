package coml.example.redislock.domain.ticket;

import coml.example.redislock.domain.member.Member;
import coml.example.redislock.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;


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
