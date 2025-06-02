package vn.minhdat.jobhunter_be.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.repository.JobRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JobRepository jobRepository;

    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
                        JobRepository jobRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobRepository = jobRepository;
    }

//    Send email with text
    public void handleSendEmail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("nguyenthangdat84@gmail.com");
        message.setSubject("Hello World");
        message.setText("Hello");

        this.mailSender.send(message);
    }

//    Send email with text and html
    public void handleSendEmailSync(String recipient, String subject, String content,
                                     boolean isMultipart, boolean isHtml){
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            this.javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println("Error in sending email");
        }
    }

//    Send email with template
    public void handleSendEmailWithTemplate(String recipient, String subject, String templateName){
        Context context = new Context();

        context.setVariable("name", "Minh Đạt");
        List<Job> jobs = this.jobRepository.findAll();
        context.setVariable("jobs", jobs);

        String content = this.templateEngine.process(templateName, context);
        this.handleSendEmailSync(recipient, subject, content, false, true);
    }
}
