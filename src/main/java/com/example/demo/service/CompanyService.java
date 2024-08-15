package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Company;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> p = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        // Lấy từ Fe
        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        // lấy từ database
        mt.setTotalPages(p.getTotalPages());
        mt.setTotalElements(p.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(p.getContent());

        return rs;
    }

    public Company getCompanyById(long id) {
        Optional<Company> c = this.companyRepository.findById(id);
        if (c.isPresent()) {
            return c.get();
        }
        return null;
    }

    public Company updateCompany(Company company) {
        Company c = this.getCompanyById(company.getId());
        if (c != null) {
            c.setName(company.getName());
            c.setAddress(company.getAddress());
            c.setDescription(company.getDescription());
            c.setLogo(company.getLogo());
            return this.handleSaveCompany(c);
        }

        return null;
    }
}
