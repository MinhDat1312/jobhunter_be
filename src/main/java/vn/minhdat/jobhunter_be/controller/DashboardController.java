package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.minhdat.jobhunter_be.common.Status;
import vn.minhdat.jobhunter_be.entity.Application;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.service.DashboardService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/users")
    public ResponseEntity<?> statisticsUser() {
        Map<String, Long> result = this.dashboardService.handleStatisticsUser();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dashboard/jobs")
    public ResponseEntity<?> statisticsJob(@Filter Specification<Job> spec) {
        Map<String, Long> result = this.dashboardService.handleStatisticsJob(spec);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dashboard/applications")
    public ResponseEntity<?> statisticsApplication(@Filter Specification<Application> spec) {
        Map<Status, Long> result = this.dashboardService.handleStatisticsApplication(spec);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dashboard/applications-year")
    public ResponseEntity<?> statisticsApplicationByYear(int year, @Filter Specification<Application> spec) {
        Map<Integer, Long> result = this.dashboardService.handleStatisticsApplicationByYear(year, spec);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
