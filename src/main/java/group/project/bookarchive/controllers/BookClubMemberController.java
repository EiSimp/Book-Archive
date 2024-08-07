package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.BookClubMember;
import group.project.bookarchive.services.BookClubMemberService;

@RestController
@RequestMapping("/bookclubmembers")
public class BookClubMemberController {

    @Autowired
    private BookClubMemberService bookClubMemberService;

    @PostMapping("/add")
    public ResponseEntity<BookClubMember> addMember(@RequestBody Map<String, String> request) {
        Long bookClubId = Long.parseLong(request.get("bookClubId"));
        Long userId = Long.parseLong(request.get("userId"));
        BookClubMember member = bookClubMemberService.addMember(bookClubId, userId);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/removeOrLeave")
    public ResponseEntity<Void> removeOrLeaveBookClub(@RequestBody Map<String, String> request) {
        Long bookClubId = Long.parseLong(request.get("bookClubId"));
        Long userId = Long.parseLong(request.get("userId"));
        Long managerUserId = Long.parseLong(request.get("managerUserId"));
        bookClubMemberService.removeOrLeaveBookClub(bookClubId, userId, managerUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members/{bookClubId}")
    public ResponseEntity<List<BookClubMember>> getMembers(@PathVariable Long bookClubId) {
        List<BookClubMember> members = bookClubMemberService.getMembers(bookClubId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/user/{userId}/bookclubs")
    public ResponseEntity<List<BookClub>> getUserBookClubs(@PathVariable Long userId) {
        List<BookClub> bookClubs = bookClubMemberService.getUserBookClubs(userId);
        return ResponseEntity.ok(bookClubs);
    }

    @GetMapping("/isMember")
    public ResponseEntity<Boolean> isUserInBookClub(@RequestParam Long bookClubId, @RequestParam Long userId) {
        boolean isMember = bookClubMemberService.isUserInBookClub(bookClubId, userId);
        return ResponseEntity.ok(isMember);
    }
}
