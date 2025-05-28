package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Skill;
import vn.minhdat.jobhunter_be.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill skill) {
        Skill currentSkill = this.handGetSkillById(skill.getSkillId());

        if(currentSkill != null) {
            currentSkill.setName(skill.getName());
            return this.skillRepository.save(currentSkill);
        }
        return null;
    }

    public void handleDeleteSkill(long id) {
        Skill currentSkill = this.handGetSkillById(id);

        if(currentSkill.getJobs() != null) {
            currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        }

        this.skillRepository.deleteById(currentSkill.getSkillId());
    }

    public Skill handGetSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);

        if(skill.isPresent()) {
            return skill.get();
        }
        return null;
    }

    public ResultPaginationResponse handleGetAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> page = this.skillRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }

    public boolean handleExistSkill(String name) {
        return this.skillRepository.existsByName(name);
    }
}
