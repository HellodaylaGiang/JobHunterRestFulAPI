package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Company;
import com.example.demo.domain.User;
import com.example.demo.domain.response.ResCreateUserDTO;
import com.example.demo.domain.response.ResUpdateUserDTO;
import com.example.demo.domain.response.ResUserDTO;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private CompanyService companyService;

    public UserService(UserRepository userRepository,
            CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User u) {
        // check company
        if (u.getCompany() != null) {
            Company c = this.companyService.getCompanyById(u.getCompany().getId());
            u.setCompany(c);
        }
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
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

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
                        item.getCreatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
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

            // check company
            if (u.getCompany() != null) {
                Company c = this.companyService.getCompanyById(u.getId());
                u.setCompany(c != null ? c : null);
            }
            return this.handleCreateUser(newU);
        }
        return newU;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User u) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(u.getId());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        res.setAge(u.getAge());
        res.setCreatedAt(u.getCreatedAt());
        res.setGender(u.getGender());
        res.setAddress(u.getAddress());

        if (u.getCompany() != null) {
            com.setId(u.getCompany().getId());
            com.setName(u.getCompany().getName());
            res.setCompanyUser(com);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User u) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();

        if (u.getCompany() != null) {
            com.setId(u.getCompany().getId());
            com.setName(u.getCompany().getName());
            res.setCompanyUser(com);
        }

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
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        if (u.getCompany() != null) {
            com.setId(u.getCompany().getId());
            com.setName(u.getCompany().getName());
            res.setCompanyUser(com);
        }

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
