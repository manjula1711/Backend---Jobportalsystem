package com.manjula.jobportal.controller;

import com.manjula.jobportal.dto.ProfileRequest;
import com.manjula.jobportal.model.Application;
import com.manjula.jobportal.model.Job;
import com.manjula.jobportal.model.RecruiterProfile;
import com.manjula.jobportal.model.User;
import com.manjula.jobportal.repository.ApplicationRepository;
import com.manjula.jobportal.repository.JobRepository;
import com.manjula.jobportal.repository.RecruiterProfileRepository;
import com.manjula.jobportal.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    // =====================================================
    // ✅ RECRUITER PROFILE (GET)
    // =====================================================
    @GetMapping("/profile")
    public ResponseEntity<?> getRecruiterProfile(Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        RecruiterProfile profile = recruiterProfileRepository.findByUser(recruiter)
                .orElseGet(() -> {
                    RecruiterProfile p = new RecruiterProfile();
                    p.setUser(recruiter);
                    return recruiterProfileRepository.save(p);
                });

        return ResponseEntity.ok(Map.of(
                "name", recruiter.getName(),
                "email", recruiter.getEmail(),
                "profile", profile
        ));
    }

    // =====================================================
    // ✅ RECRUITER PROFILE (UPDATE)
    // =====================================================
    @PutMapping("/profile")
    public ResponseEntity<?> updateRecruiterProfile(@RequestBody ProfileRequest req,
                                                    Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        RecruiterProfile profile = recruiterProfileRepository.findByUser(recruiter)
                .orElseGet(() -> {
                    RecruiterProfile p = new RecruiterProfile();
                    p.setUser(recruiter);
                    return p;
                });

        profile.setCompanyName(req.getCompanyName());
        profile.setCompanyLocation(req.getCompanyLocation());
        profile.setCompanyWebsite(req.getCompanyWebsite());
        profile.setDescription(req.getDescription());

        recruiterProfileRepository.save(profile);

        return ResponseEntity.ok(Map.of(
                "name", recruiter.getName(),
                "email", recruiter.getEmail(),
                "profile", profile
        ));
    }

    // =====================================================
    // 🔹 POST JOB
    // =====================================================
    @PostMapping("/jobs")
    public ResponseEntity<?> postJob(@RequestBody Job job,
                                     Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        job.setPostedBy(recruiter);
        job.setActive(true);

        return ResponseEntity.ok(jobRepository.save(job));
    }

    // =====================================================
    // 🔹 GET MY JOBS
    // =====================================================
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getMyJobs(Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(jobRepository.findByPostedBy(recruiter));
    }

    // =====================================================
    // 🔹 DELETE MY JOB (SECURE)
    // =====================================================
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id,
                                       Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        Job job = jobRepository.findById(id).orElseThrow();

        if (!job.getPostedBy().getId().equals(recruiter.getId())) {
            return ResponseEntity.badRequest().body("You cannot delete this job");
        }

        jobRepository.delete(job);
        return ResponseEntity.ok("Job deleted successfully");
    }
    
 // =====================================================
 // 🔹 UPDATE MY JOB (SECURE)
 // =====================================================
    @PutMapping("/jobs/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id,
                                       @RequestBody Job req,
                                       Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        Job job = jobRepository.findById(id).orElseThrow();

        if (!job.getPostedBy().getId().equals(recruiter.getId())) {
            return ResponseEntity.badRequest().body("You cannot update this job");
        }

        // ✅ update editable fields (NO type)
        job.setCompanyName(req.getCompanyName());
        job.setTitle(req.getTitle());
        job.setLocation(req.getLocation());
        job.setSalary(req.getSalary());
        job.setExperience(req.getExperience());
        job.setSkills(req.getSkills());
        job.setDescription(req.getDescription());

        job.setActive(req.isActive());

        return ResponseEntity.ok(jobRepository.save(job));
    }
    
    

    // =====================================================
    // 🔹 VIEW APPLICANTS FOR MY JOBS
    // =====================================================
    @GetMapping("/applicants")
    public ResponseEntity<List<Application>> getApplicants(Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(applicationRepository.findByJob_PostedBy(recruiter));
    }

    // =====================================================
    // 🔹 VIEW / DOWNLOAD RESUME PDF (SECURE)
    // =====================================================
    @GetMapping("/applications/{id}/resume")
    public ResponseEntity<Resource> viewResume(@PathVariable Long id,
                                               Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        Application application = applicationRepository.findById(id).orElseThrow();

        if (!application.getJob().getPostedBy().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(403).build();
        }

        File file = new File(application.getResumePath());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    // =====================================================
    // 🔹 UPDATE APPLICATION STATUS (SECURE)
    // =====================================================
    @PutMapping("/applications/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        Application application = applicationRepository.findById(id).orElseThrow();

        if (!application.getJob().getPostedBy().getId().equals(recruiter.getId())) {
            return ResponseEntity.badRequest().body("You cannot update this application");
        }

        application.setStatus(status.toUpperCase());
        applicationRepository.save(application);

        return ResponseEntity.ok("Status updated successfully");
    }

    // =====================================================
    // 🔹 RECRUITER DASHBOARD
    // =====================================================
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(Authentication authentication) {

        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email).orElseThrow();

        Map<String, Object> data = new HashMap<>();

        long totalJobs = jobRepository.countByPostedBy(recruiter);
        long activeJobs = jobRepository.countByPostedByAndActiveTrue(recruiter);
        long totalApplications = applicationRepository.countByJob_PostedBy(recruiter);

        List<Job> recentJobs = jobRepository.findTop5ByPostedByOrderByIdDesc(recruiter);

        List<Application> recentApplications =
                applicationRepository.findTop5ByJob_PostedByOrderByIdDesc(recruiter);

        data.put("totalJobs", totalJobs);
        data.put("activeJobs", activeJobs);
        data.put("totalApplications", totalApplications);
        data.put("recentJobs", recentJobs);
        data.put("recentApplications", recentApplications);

        return ResponseEntity.ok(data);
    }
}