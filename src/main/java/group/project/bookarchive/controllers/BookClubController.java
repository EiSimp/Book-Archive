package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.services.BookClubService;

@RestController
@RequestMapping("/bookclubs")
public class BookClubController {

    @Autowired
    private BookClubService bookClubService;

    @PostMapping("/create")
    public ResponseEntity<BookClub> createBookClub(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        BookClub bookClub = bookClubService.createBookClub(name, description);
        return ResponseEntity.ok(bookClub);
    }

    @PostMapping("/rename")
    public ResponseEntity<BookClub> renameBookClub(@RequestBody Map<String, String> request) {
        Long id = Long.parseLong(request.get("id"));
        String newName = request.get("newName");
        Long userId = Long.parseLong(request.get("userId"));
        BookClub bookClub = bookClubService.renameBookClub(id, newName, userId);
        return ResponseEntity.ok(bookClub);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteBookClub(@RequestBody Map<String, String> request) {
        Long id = Long.parseLong(request.get("id"));
        Long userId = Long.parseLong(request.get("userId"));
        bookClubService.deleteBookClub(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferManager(@RequestBody Map<String, String> request) {
        Long bookClubId = Long.parseLong(request.get("bookClubId"));
        Long newManagerUserId = Long.parseLong(request.get("newManagerUserId"));
        Long currentManagerUserId = Long.parseLong(request.get("currentManagerUserId"));
        bookClubService.transferManager(bookClubId, newManagerUserId, currentManagerUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookClub>> getAllBookClubs() {
        List<BookClub> bookClubs = bookClubService.getAllBookClubs();
        return ResponseEntity.ok(bookClubs);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<BookClub> getBookClubDetails(@PathVariable Long id) {
        BookClub bookClub = bookClubService.getBookClubDetails(id);
        return ResponseEntity.ok(bookClub);
    }
}
