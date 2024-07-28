package com.example.demo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Company;
import com.example.demo.domain.dto.ResultPaginationDTO;
import com.example.demo.service.CompanyService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> create(@Valid @RequestBody Company company) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleSaveCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.companyService.getCompanyById(id));
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable
    // @RequestParam("currentPage") Optional<String> currentPage,
    // @RequestParam("pageSize") Optional<String> pageSize
    ) {
        // String sCurrentPage = currentPage.isPresent() ? currentPage.get() : "";
        // String sPageSize = pageSize.isPresent() ? pageSize.get() : "";

        // int current = Integer.parseInt(sCurrentPage);
        // int size = Integer.parseInt(sPageSize);
        // // Pageable không có hàm tạo => PageRequest kế thừa Pageable
        // Pageable pageable = PageRequest.of(current - 1, size);
        return ResponseEntity.ok().body(this.companyService.getAllCompany(spec, pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok().body(this.companyService.updateCompany(company));
    }

}
