package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.dto.response.UserResponse;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByContact_Email(email);
    }

    public boolean handleExistsByEmail(String email) {
        return this.userRepository.existsByContact_Email(email);
    }

    public void handleUpdateRefreshToken(String email, String refreshToken) {
        User user = this.userRepository.findByContact_Email(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndContact_Email(refreshToken, email);
    }

    public ResultPaginationResponse handleGetAllUsers (Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<UserResponse> userResponses = page.getContent().stream()
                                                            .map(this::convertToUserResponse)
                                                            .collect(Collectors.toList());

        return new ResultPaginationResponse(meta, userResponses);
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setUserId(user.getUserId());
        userResponse.setContact(user.getContact());
        userResponse.setAddress(user.getAddress());
        userResponse.setUsername(user.getUsername());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }
}
