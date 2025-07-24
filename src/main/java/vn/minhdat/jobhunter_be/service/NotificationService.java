package vn.minhdat.jobhunter_be.service;

import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
