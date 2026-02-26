package com.manjula.jobportal.controller;

import com.manjula.jobportal.model.Job;
import com.manjula.jobportal.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // allow frontend access
public class PublicJobController {

    @Autowired
    private JobRepository jobRepository;

    // 🔹 Get all active jobs (Public)
    @GetMapping("/jobs")
    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    // 🔹 Get job details by ID
    @GetMapping("/jobs/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}