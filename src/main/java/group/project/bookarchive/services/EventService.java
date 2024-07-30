package group.project.bookarchive.services;

import group.project.bookarchive.models.Event;
import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.EventRepository;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookClubRepository bookClubRepository;

    @Autowired
    private UserRepository userRepository;

    public Event createEvent(Event event) {
        BookClub bookClub = bookClubRepository.findById(event.getBookClub().getId())
                .orElseThrow(() -> new RuntimeException("Book club not found"));

        User currentUser = getCurrentUser();
        if (!bookClub.getManager().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the manager can create events");
        }

        event.setBookClub(bookClub);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByBookClub(Long bookClubId) {
        return eventRepository.findByBookClubId(bookClubId);
    }

    public List<Event> getEventsByBookClubAndMonth(Long bookClubId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return eventRepository.findByBookClubIdAndDateTimeBetween(bookClubId, start, end);
    }

    public Event getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        checkManagerAccess(event.getBookClub().getId());
        return event;
    }

    public Event updateEvent(Long eventId, Event eventDetails) {
        Event event = getEventById(eventId);
        checkManagerAccess(event.getBookClub().getId());
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setDateTime(eventDetails.getDateTime());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        Event event = getEventById(eventId);
        checkManagerAccess(event.getBookClub().getId());
        eventRepository.delete(event);
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void checkManagerAccess(Long bookClubId) {
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book club not found"));
        User currentUser = getCurrentUser();
        if (!bookClub.getManager().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the manager can access this resource");
        }
    }
}
