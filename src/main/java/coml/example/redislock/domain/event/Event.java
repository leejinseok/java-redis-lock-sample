package coml.example.redislock.domain.event;

import coml.example.redislock.domain.ticket.Ticket;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long eventLimit;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    public static Event of(long eventLimit) {
        Event event = new Event();
        event.eventLimit = eventLimit;
        return event;
    }

    public void addTicket(Ticket ticket) {
        ticket.updateEvent(this);
        tickets.add(ticket);
    }


}
