package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.common.NotificationType;
import vn.minhdat.jobhunter_be.dto.request.LikeBlogRequest;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Comment;
import vn.minhdat.jobhunter_be.entity.Notification;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.repository.BlogRepository;
import vn.minhdat.jobhunter_be.repository.CommentRepository;
import vn.minhdat.jobhunter_be.repository.NotificationRepository;
import vn.minhdat.jobhunter_be.repository.UserRepository;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

import java.util.List;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public BlogService(BlogRepository blogRepository, CommentRepository commentRepository,
                       NotificationRepository notificationRepository, UserRepository userRepository
    ) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Blog handleCreateBlog(Blog blog) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userRepository.findByContact_Email(email);
        blog.setAuthor(currentUser);
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
            comments.forEach(this::deleteRecursively);
        }
        if(blog.getNotifications() != null) {
            List<Notification> notifications = blog.getNotifications();
            this.notificationRepository.deleteAll(notifications);
        }

        this.blogRepository.delete(blog);
    }

    private void deleteRecursively(Comment comment) {
        if (comment.getChildren() != null) {
            for (Comment child : comment.getChildren()) {
                deleteRecursively(child);
            }
        }

        if(comment.getCommentNotifications() != null) {
            List<Notification> notifications = comment.getCommentNotifications();
            this.notificationRepository.deleteAll(notifications);
        }
        if(comment.getReplyNotifications() != null) {
            List<Notification> notifications = comment.getReplyNotifications();
            this.notificationRepository.deleteAll(notifications);
        }
        if(comment.getRepliedOnCommentNotifications() != null) {
            List<Notification> notifications = comment.getRepliedOnCommentNotifications();
            this.notificationRepository.deleteAll(notifications);
        }

        this.commentRepository.delete(comment);
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

    public void handleIncrementTotalValue(Comment comment) {
        Blog blog = comment.getBlog();
        blog.getActivity().setTotalComments(blog.getActivity().getTotalComments() + 1);
        if(!comment.isReply()) {
            blog.getActivity().setTotalParentComments(blog.getActivity().getTotalParentComments() + 1);
        }
        this.blogRepository.save(blog);
    }

    public Blog handleLikeBlog(LikeBlogRequest likeBlogRequest) {
        int incrementVal = likeBlogRequest.isLiked() ? 1 : -1;
        Blog blog = this.handleGetBlogById(likeBlogRequest.getBlog().getBlogId());
        long newTotalLikes = blog.getActivity().getTotalLikes() + incrementVal;
        blog.getActivity().setTotalLikes(Math.max(newTotalLikes, 0));
        Blog updatedBlog = this.blogRepository.save(blog);

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userRepository.findByContact_Email(email);

        if(likeBlogRequest.isLiked()) {
            Notification notification = new Notification();
            notification.setType(NotificationType.LIKE);
            notification.setSeen(false);
            notification.setBlog(updatedBlog);
            notification.setActor(currentUser);
            notification.setRecipient(blog.getAuthor());

            this.notificationRepository.save(notification);
        }

        return updatedBlog;
    }

    public List<String> handleGetAllTags(String keyword) {
        return this.blogRepository.findAllTags(keyword);
    }
}
