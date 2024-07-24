package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User u) {
        return this.userRepository.save(u);
    }

    public User findUserById(long id) {
        Optional<User> u = this.userRepository.findById(id);
        if (u.isPresent()) {
            return u.get();
        }
        return null;
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public User updateUser(User u) {
        User newU = this.findUserById(u.getId());
        newU.setName(u.getName());
        newU.setEmail(u.getEmail());
        newU.setPassword(u.getPassword());
        this.handleCreateUser(newU);
        return newU;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
