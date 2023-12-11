package coml.example.redislock.presentation;

import coml.example.redislock.domain.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final TicketService ticketService;

    @PostMapping("/{eventId}/reserveTicket")
    public void reserveTicket(@PathVariable Long eventId, @RequestParam Long memberId) {
        ticketService.reserveTicket(eventId, memberId);
        log.info("EventController > reserveTicket > success!!");
    }

}
