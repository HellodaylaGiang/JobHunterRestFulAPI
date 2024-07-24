package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.error.IdInvalidException;

@RestController
public class UserController {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users/create")
    public ResponseEntity<User> createNewUser(@RequestBody User u) {
        String hashPass = passwordEncoder.encode(u.getPassword());
        u.setPassword(hashPass);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(u));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {

        if (id > 1500) {
            throw new IdInvalidException("khong ton tai id: " + id);
        }
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete a user");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) throws IdInvalidException {

        if (id > 1500) {
            throw new IdInvalidException("khong ton tai id: " + id);
        }

        User u = this.userService.findUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUser());
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User u) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUser(u));
    }

}
