package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Company;
import com.example.demo.domain.Job;
import com.example.demo.domain.Resume;
import com.example.demo.domain.User;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.resume.ResCreateResumeDTO;
import com.example.demo.domain.response.resume.ResFetchResumeDTO;
import com.example.demo.domain.response.resume.ResUpdateResumeDTO;
import com.example.demo.service.ResumeService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService,
            UserService userService,
            FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume r) throws IdInvalidException {

        boolean checkExist = this.resumeService.checkResumeExistByUserAndJob(r);

        if (!checkExist) {
            throw new IdInvalidException("User/Job không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(r));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume r) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(r.getId());

        if (resume == null) {
            throw new IdInvalidException("Resume với id = " + r.getId() + " không tồn tại");
        }

        resume.setStatus(r.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume id= " + id + " không tồn tại");
        }

        this.resumeService.delete(resume);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume id= " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.resumeService.convertGetResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobs = null;
        //
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        // Lấy user theo email
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            // Lấy company theo user
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                // Lấy job theo company
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobs = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInspec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobs)).get());

        Specification<Resume> finalSpec = jobInspec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAll(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
