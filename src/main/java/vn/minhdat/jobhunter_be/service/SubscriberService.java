package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.EmailJobResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.entity.Skill;
import vn.minhdat.jobhunter_be.entity.Subscriber;
import vn.minhdat.jobhunter_be.repository.JobRepository;
import vn.minhdat.jobhunter_be.repository.SkillRepository;
import vn.minhdat.jobhunter_be.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
                             JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber handleCreateSubscriber(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Long> idSkills = subscriber.getSkills().stream().map(Skill::getSkillId)
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findBySkillIdIn(idSkills);
            subscriber.setSkills(skills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber subscriber) {
        Subscriber currentSubscriber = this.handleGetSubscriberById(subscriber.getSubscriberId());

        if (subscriber.getSkills() != null) {
            List<Long> idSkills = subscriber.getSkills().stream().map(Skill::getSkillId)
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findBySkillIdIn(idSkills);
            subscriber.setSkills(skills);
        }
        currentSubscriber.setSkills(subscriber.getSkills());

        return this.subscriberRepository.save(currentSubscriber);
    }

    public void handleDeleteSubscriber(long id) {
        Subscriber subscriber = this.handleGetSubscriberById(id);
        this.jobRepository.deleteById(id);
    }

    public Subscriber handleGetSubscriberById(long id) {
        Optional<Subscriber> optional = this.subscriberRepository.findById(id);
        return optional.orElse(null);
    }

    public ResultPaginationResponse handleGetAllSubscribers(Specification<Subscriber> spec, Pageable pageable) {
        Page<Subscriber> page = this.subscriberRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }

    public Subscriber handleGetSubscribersSkill(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public Subscriber handleGetSubscriberByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public void handleSendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (!listSubs.isEmpty()) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && !listSkills.isEmpty()) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && !listJobs.isEmpty()) {
                        List<EmailJobResponse> arr = listJobs.stream().map(
                                this::convertJobToSendEmail).collect(Collectors.toList()
                        );
                        this.emailService.handleSendEmailWithTemplate(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public EmailJobResponse convertJobToSendEmail(Job job) {
        EmailJobResponse res = new EmailJobResponse();
        res.setTitle(job.getTitle());
        res.setSalary(job.getSalary());
        res.setRecruiter(new EmailJobResponse.RecruiterEmail(job.getRecruiter().getFullName()));
        List<Skill> skills = job.getSkills();
        List<EmailJobResponse.SkillEmail> skillResponses = skills.stream()
                .map(skill -> new EmailJobResponse.SkillEmail(skill.getName()))
                .toList();
        res.setSkills(skillResponses);

        return res;
    }
}
