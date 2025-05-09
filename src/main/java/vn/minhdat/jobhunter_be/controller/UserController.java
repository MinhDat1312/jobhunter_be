package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationResponse> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable
    ) {
        ResultPaginationResponse result = this.userService.handleGetAllUsers(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
