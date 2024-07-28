package com.example.demo.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.ResultPaginationDTO;
import com.example.demo.service.UserService;
import com.example.demo.util.error.IdInvalidException;

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
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @RequestParam("currentPage") Optional<String> currentPage,
            @RequestParam("pageSize") Optional<String> pageSize) {
        String sCurrentPage = currentPage.isPresent() ? currentPage.get() : "";
        String sPageSize = pageSize.isPresent() ? pageSize.get() : "";

        int current = Integer.parseInt(sCurrentPage);
        int size = Integer.parseInt(sPageSize);
        // Pageable không có hàm tạo => PageRequest kế thừa Pageable
        Pageable pageable = PageRequest.of(current - 1, size);
        return ResponseEntity.ok().body(this.userService.getAllUser(pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User u) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUser(u));
    }

}
