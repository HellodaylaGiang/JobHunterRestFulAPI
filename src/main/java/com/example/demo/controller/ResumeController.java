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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Resume;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.domain.response.resume.ResCreateResumeDTO;
import com.example.demo.domain.response.resume.ResFetchResumeDTO;
import com.example.demo.domain.response.resume.ResUpdateResumeDTO;
import com.example.demo.service.ResumeService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
        return ResponseEntity.ok().body(this.resumeService.fetchAll(spec, pageable));
    }

}
