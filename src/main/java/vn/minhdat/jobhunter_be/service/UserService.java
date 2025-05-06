package vn.minhdat.jobhunter_be.service;

import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByContact_Email(email);
    }
}
