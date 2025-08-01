package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.request.LikeBlogRequest;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Notification;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.BlogService;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/blogs")
    public ResponseEntity<?> createBlog(@Valid @RequestBody Blog blog) {
        Blog res = this.blogService.handleCreateBlog(blog);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/blogs")
    public ResponseEntity<?> updateBlog(@Valid @RequestBody Blog blog) throws InvalidException {
        Blog res = this.blogService.handleUpdateBlog(blog);
        if(res == null) throw new InvalidException("Blog doesn't exist");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(!pattern.matcher(id).matches()) throw new InvalidException("Id is number");

        Blog res = this.blogService.handleGetBlogById(Long.parseLong(id));
        if(res == null) throw new InvalidException("Blog doesn't exist");

        this.blogService.handleDeleteBlog(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/blogs/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(!pattern.matcher(id).matches()) throw new InvalidException("Id is number");

        Blog res = this.blogService.handleGetBlogById(Long.parseLong(id));
        if(res == null) throw new InvalidException("Blog doesn't exist");

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/blogs")
    public ResponseEntity<ResultPaginationResponse> getAllBlogs(
            @Filter Specification<Blog> spec, Pageable pageable
    ) {
        ResultPaginationResponse res = this.blogService.handleGetAllBlogs(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/blogs/liked-blogs")
    public ResponseEntity<List<Notification>> likeBlogs(@Valid @RequestBody LikeBlogRequest likeBlogRequest) {
        List<Notification> res = this.blogService.handleLikeBlog(likeBlogRequest);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/blogs/tags")
    public ResponseEntity<List<String>> getAllTags(String keyword) {
        List<String> res = this.blogService.handleGetAllTags(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
