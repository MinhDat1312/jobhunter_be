package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Comment;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.repository.BlogRepository;
import vn.minhdat.jobhunter_be.repository.CommentRepository;
import vn.minhdat.jobhunter_be.repository.UserRepository;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository,
                          UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    public Comment handleCreateComment(Comment comment) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userRepository.findByContact_Email(email);
        comment.setCommentedBy(currentUser);
        if(comment.getBlog() != null) {
            Blog blog = this.blogRepository.findById(comment.getBlog().getBlogId()).orElse(null);
            if(blog != null) {
                comment.setBlog(blog);
            }
        }
        if(comment.getParent() != null) {
            Comment parentComment = this.handleGetCommentById(comment.getParent().getCommentId());
            if(parentComment != null) {
                comment.setParent(parentComment);
                comment.setReply(true);
            }
        } else {
            comment.setReply(false);
        }
        Comment newComment = this.commentRepository.save(comment);

        Blog blog = newComment.getBlog();
        blog.getActivity().setTotalComments(blog.getActivity().getTotalComments() + 1);
        if(!newComment.isReply()) {
            blog.getActivity().setTotalParentComments(blog.getActivity().getTotalParentComments() + 1);
        }
        this.blogRepository.save(blog);

        return newComment;
    }

    public Comment handleUpdateComment(Comment comment) {
        Comment currentComment = this.handleGetCommentById(comment.getCommentId());
        if(currentComment == null) return null;

        currentComment.setComment(comment.getComment());
        return this.commentRepository.save(currentComment);
    }

    public void handleDeleteComment(long id) {
        Comment comment = this.handleGetCommentById(id);
        Blog blog = comment.getBlog();
        int deletedCount = deleteRecursively(comment);

        blog.getActivity().setTotalComments(blog.getActivity().getTotalComments() - deletedCount);
        if(!comment.isReply()) {
            blog.getActivity().setTotalParentComments(blog.getActivity().getTotalParentComments() - 1);
        }

        this.blogRepository.save(blog);
    }

    private int deleteRecursively(Comment comment) {
        int count = 1;
        if (comment.getChildren() != null) {
            for (Comment child : comment.getChildren()) {
                count += deleteRecursively(child);
            }
        }
        this.commentRepository.delete(comment);
        return count;
    }

    public Comment handleGetCommentById(long id) {
        return this.commentRepository.findById(id).orElse(null);
    }

    public ResultPaginationResponse handleGetAllComments(Specification<Comment> spec, Pageable pageable) {
        Page<Comment> page = this.commentRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }
}
