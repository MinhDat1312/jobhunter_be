package vn.minhdat.jobhunter_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.minhdat.jobhunter_be.service.SubscriberService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
//    @Scheduled(cron = "*/10 * * * * *")
//    @Transactional
    public void sendEmail(){
        this.subscriberService.handleSendSubscribersEmailJobs();
        this.subscriberService.handleSendFollowersEmailJobs();
    }

}
