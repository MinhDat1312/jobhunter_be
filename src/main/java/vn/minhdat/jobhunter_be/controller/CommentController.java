package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Comment;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.BlogService;
import vn.minhdat.jobhunter_be.service.CommentService;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;
    private final BlogService blogService;

    public CommentController(CommentService commentService, BlogService blogService) {
        this.commentService = commentService;
        this.blogService = blogService;
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) throws InvalidException {
        Blog blog = this.blogService.handleGetBlogById(comment.getBlog().getBlogId());
        if(blog == null) throw new InvalidException("Blog doesn't exist");

        Comment res = this.commentService.handleCreateComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/comments")
    public ResponseEntity<Comment> updateComment(@Valid @RequestBody Comment comment) throws InvalidException {
        Comment res = this.commentService.handleUpdateComment(comment);
        if(res == null) throw new InvalidException("Comment doesn't exist");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(!pattern.matcher(id).matches()) throw new InvalidException("Id is number");

        Comment res = this.commentService.handleGetCommentById(Long.parseLong(id));
        if(res == null) throw new InvalidException("Comment doesn't exist");

        this.commentService.handleDeleteComment(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(!pattern.matcher(id).matches()) throw new InvalidException("Id is number");

        Comment res = this.commentService.handleGetCommentById(Long.parseLong(id));
        if(res == null) throw new InvalidException("Comment doesn't exist");

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/comments")
    public ResponseEntity<ResultPaginationResponse> getAllComments(
            @Filter Specification<Comment> spec, Pageable pageable
    ) {
        ResultPaginationResponse res = this.commentService.handleGetAllComments(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
