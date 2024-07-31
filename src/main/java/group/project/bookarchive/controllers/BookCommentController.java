package group.project.bookarchive.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group.project.bookarchive.models.BookComment;
import group.project.bookarchive.models.BookCommentWithRatingDTO;
import group.project.bookarchive.services.BookCommentService;

@RestController
@RequestMapping("/comments")
public class BookCommentController {

    @Autowired
    private BookCommentService bookCommentService;

    @PostMapping("/add")
    public ResponseEntity<Void> addComment(@RequestParam String username,
            @RequestParam String googleBookId,
            @RequestParam String commentText) {

        bookCommentService.addComment(username, googleBookId, commentText);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId,
            @RequestParam String newCommentText) {
        bookCommentService.updateComment(commentId, newCommentText);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        bookCommentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/book/{googleBookId}")
    public ResponseEntity<List<BookCommentWithRatingDTO>> getCommentsForBook(@PathVariable String googleBookId) {
        List<BookCommentWithRatingDTO> comments = bookCommentService.getCommentsWithRatings(googleBookId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BookComment>> getCommentsFromUser(@PathVariable Long id) {
        List<BookComment> comments = bookCommentService.getCommentsfromUser(id);
        return ResponseEntity.ok(comments);
    }

}