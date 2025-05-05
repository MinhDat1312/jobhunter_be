package vn.minhdat.jobhunter_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.UserService;

import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updateUser) {
        User newUser = this.userService.handleUpdateUser(updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(pattern.matcher(id).matches()){
            User user = this.userService.handleGetUserById(Long.parseLong(id));
            if(user != null){
                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                throw new InvalidException("User not found");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ArrayList<User>> getAllUsers() {
        ArrayList<User> users = this.userService.handleGetAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
