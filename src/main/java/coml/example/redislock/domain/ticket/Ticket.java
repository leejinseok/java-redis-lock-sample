package coml.example.redislock.domain.ticket;

import coml.example.redislock.domain.event.Event;
import coml.example.redislock.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Ticket of(Event event) {
        Ticket ticket = new Ticket();
        ticket.event = event;
        return ticket;
    }

    public void updateEvent(Event event) {
        this.event = event;
    }

    public void reservedBy(Member member) {
        this.member = member;
    }

}
