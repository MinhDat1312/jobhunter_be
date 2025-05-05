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

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleUpdateUser(User updateUser) {
        User currentUser = this.handleGetUserById(updateUser.getUserId());

        if(currentUser != null) {
            currentUser.setAddress(updateUser.getAddress());
            currentUser.setContact(updateUser.getContact());
            currentUser.setDob(updateUser.getDob());
            currentUser.setFullName(updateUser.getFullName());
            currentUser.setGender(updateUser.getGender());
            currentUser.setPassword(updateUser.getPassword());
            currentUser.setUsername(updateUser.getUsername());

            return this.userRepository.save(currentUser);
        }

        return null;
    }

    public User handleGetUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()) {
            return user.get();
        }

        return null;
    }

    public ArrayList<User> handleGetAllUsers() {
        return (ArrayList<User>) this.userRepository.findAll();
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByContact_Email(email);
    }
}
