package coml.example.redislock.presentation;

import coml.example.redislock.domain.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final TicketService ticketService;

    @PostMapping("/{eventId}/reserveTicket")
    public void reserveTicket(@PathVariable Long eventId, @RequestParam Long memberId) {
        ticketService.reserveTicket(eventId, memberId);
    }

}
