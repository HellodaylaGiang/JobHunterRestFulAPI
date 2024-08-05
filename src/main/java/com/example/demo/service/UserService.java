package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.Meta;
import com.example.demo.domain.dto.ResCreateUserDTO;
import com.example.demo.domain.dto.ResUpdateUserDTO;
import com.example.demo.domain.dto.ResUserDTO;
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

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> p = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        // Lấy từ Fe
        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        // lấy từ database
        mt.setTotalPages(p.getTotalPages());
        mt.setTotalElements(p.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listU = p.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
                .collect(Collectors.toList());

        rs.setResult(listU);

        return rs;
    }

    public User updateUser(User u) {
        User newU = this.findUserById(u.getId());
        if (newU != null) {
            newU.setName(u.getName());
            newU.setAddress(u.getAddress());
            newU.setAge(u.getAge());
            newU.setGender(u.getGender());
            return this.handleCreateUser(newU);
        }
        return newU;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User u) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setCreatedAt(u.getCreatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());

        return res;
    }

    public ResUserDTO convertToResUserDTO(User u) {
        ResUserDTO res = new ResUserDTO();
        res.setId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setUpdatedAt(u.getUpdatedAt());
        res.setCreatedAt(u.getCreatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User u) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(u.getId());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setUpdatedAt(u.getUpdatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentU = this.handleGetUserByUsername(email);
        if (currentU != null) {
            currentU.setRefreshToken(token);
            this.userRepository.save(currentU);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
