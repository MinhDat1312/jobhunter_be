package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Comment;
import vn.minhdat.jobhunter_be.entity.Notification;
import vn.minhdat.jobhunter_be.repository.BlogRepository;
import vn.minhdat.jobhunter_be.repository.CommentRepository;
import vn.minhdat.jobhunter_be.repository.NotificationRepository;

import java.util.List;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public BlogService(BlogRepository blogRepository, CommentRepository commentRepository,
                       NotificationRepository notificationRepository
    ) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
    }

    public Blog handleCreateBlog(Blog blog) {
        return this.blogRepository.save(blog);
    }

    public Blog handleUpdateBlog(Blog blog) {
        Blog currentBlog = this.handleGetBlogById(blog.getBlogId());

        if(currentBlog != null) {
            currentBlog.setTitle(blog.getTitle());
            currentBlog.setBanner(blog.getBanner());
            currentBlog.setDescription(blog.getDescription());
            currentBlog.setContent(blog.getContent());
            currentBlog.setTags(blog.getTags());
            currentBlog.setDraft(blog.isDraft());
            currentBlog.setActivity(blog.getActivity());

            return this.blogRepository.save(currentBlog);
        }

        return null;
    }

    public void handleDeleteBlog(long id) {
        Blog blog = this.handleGetBlogById(id);

        if(blog.getComments() != null) {
            List<Comment> comments = blog.getComments();
            this.commentRepository.deleteAll(comments);
        }
        if(blog.getNotifications() != null) {
            List<Notification> notifications = blog.getNotifications();
            this.notificationRepository.deleteAll(notifications);
        }

        this.blogRepository.delete(blog);
    }

    public Blog handleGetBlogById(long id) {
        return this.blogRepository.findById(id).orElse(null);
    }

    public ResultPaginationResponse handleGetAllBlogs(Specification<Blog> spec, Pageable pageable) {
        Page<Blog> page = this.blogRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }
}
