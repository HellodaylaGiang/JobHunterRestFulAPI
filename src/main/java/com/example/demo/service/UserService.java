package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.Meta;
import com.example.demo.domain.dto.ResultPaginationDTO;
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

    public ResultPaginationDTO getAllUser(Pageable pageable) {
        Page<User> p = this.userRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setCurrentPage(p.getNumber() + 1);
        mt.setPageSize(p.getSize());

        mt.setTotalPages(p.getTotalPages());
        mt.setTotalElements(p.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(p.getContent());

        return rs;
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
