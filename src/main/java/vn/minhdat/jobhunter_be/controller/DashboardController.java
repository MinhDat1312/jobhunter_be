package vn.minhdat.jobhunter_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.minhdat.jobhunter_be.common.Status;
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
        Map<String, Long> result = this.dashboardService.statisticsUser();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dashboard/jobs")
    public ResponseEntity<?> statisticsJob() {
        Map<String, Long> result = this.dashboardService.statisticsJob();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/dashboard/applications")
    public ResponseEntity<?> statisticsApplication() {
        Map<Status, Long> result = this.dashboardService.statisticsApplication();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
