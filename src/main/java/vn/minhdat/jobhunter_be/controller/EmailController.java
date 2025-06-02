package vn.minhdat.jobhunter_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.minhdat.jobhunter_be.service.EmailService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String sendEmail(){
//        this.emailService.handleSendEmail();

//        this.emailService.handleSendEmailSync(
//                "nguyenthangdat84@gmail.com",
//                "Hello World",
//                "<h1><b>MinhDat</b></h1>",
//                false, true
//        );

        this.emailService.handleSendEmailWithTemplate(
                "nguyenthangdat84@gmail.com",
                "Hello World",
                "job"
        );
        return "ok";
    }
}
