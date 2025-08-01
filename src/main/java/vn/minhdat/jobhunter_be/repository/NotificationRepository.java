package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.minhdat.jobhunter_be.common.NotificationType;
import vn.minhdat.jobhunter_be.entity.Blog;
import vn.minhdat.jobhunter_be.entity.Notification;
import vn.minhdat.jobhunter_be.entity.User;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Optional<Notification> findByTypeAndActorAndBlogAndRecipient(
            NotificationType type,
            User actor,
            Blog blog,
            User recipient
    );
}
