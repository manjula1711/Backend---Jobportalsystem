package com.manjula.jobportal.controller;

import com.manjula.jobportal.dto.ProfileRequest;
import com.manjula.jobportal.model.*;
import com.manjula.jobportal.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/seeker")
public class SeekerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SeekerProfileRepository seekerProfileRepository;

    // =====================================================
    // ✅ SEEKER PROFILE (GET)
    // =====================================================
    @GetMapping("/profile")
    public ResponseEntity<?> getSeekerProfile(Authentication authentication) {

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        SeekerProfile profile = seekerProfileRepository.findByUser(seeker)
                .orElseGet(() -> {
                    SeekerProfile p = new SeekerProfile();
                    p.setUser(seeker);
                    return seekerProfileRepository.save(p);
                });

        return ResponseEntity.ok(Map.of(
                "name", seeker.getName(),
                "email", seeker.getEmail(),
                "profile", profile
        ));
    }

    // =====================================================
    // ✅ SEEKER PROFILE (UPDATE)
    // =====================================================
    @PutMapping("/profile")
    public ResponseEntity<?> updateSeekerProfile(@RequestBody ProfileRequest req,
                                                 Authentication authentication) {

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        SeekerProfile profile = seekerProfileRepository.findByUser(seeker)
                .orElseGet(() -> {
                    SeekerProfile p = new SeekerProfile();
                    p.setUser(seeker);
                    return p;
                });

        profile.setPhone(req.getPhone());
        profile.setLocation(req.getLocation());
        profile.setSkills(req.getSkills());
        profile.setExperience(req.getExperience());
        profile.setEducation(req.getEducation());

        seekerProfileRepository.save(profile);

        return ResponseEntity.ok(Map.of(
                "name", seeker.getName(),
                "email", seeker.getEmail(),
                "profile", profile
        ));
    }

    // =====================================================
    // 🔹 APPLY JOB
    // =====================================================
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyJob(
            @PathVariable Long jobId,
            @RequestParam String phone,
            @RequestParam(required = false) String coverLetter,   // ✅ optional
            @RequestParam MultipartFile resume,
            Authentication authentication
    ) throws IOException {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        Job job = jobRepository.findById(jobId).orElseThrow();

        if (!job.isActive()) {
            return ResponseEntity.badRequest().body("Job is not active");
        }

        if (applicationRepository.findByJobAndSeeker(job, seeker).isPresent()) {
            return ResponseEntity.badRequest().body("Already Applied");
        }

        // ✅ Upload Resume
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File directory = new File(uploadDir);
        if (!directory.exists()) directory.mkdirs();

        String uniqueFileName = UUID.randomUUID() + "_" + resume.getOriginalFilename();
        String filePath = uploadDir + File.separator + uniqueFileName;

        resume.transferTo(new File(filePath));

        // ✅ Save Application
        Application application = new Application();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setPhone(phone);
        application.setCoverLetter(coverLetter == null ? "" : coverLetter);
        application.setResumePath(filePath);
        application.setStatus("PENDING");

        // ✅ IMPORTANT: set applied date (for UI table)
        application.setAppliedDate(LocalDateTime.now());

        applicationRepository.save(application);

        return ResponseEntity.ok("Application Submitted Successfully");
    }

    // =====================================================
    // 🔹 MY APPLICATIONS
    // =====================================================
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> myApplications(Authentication authentication) {

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(applicationRepository.findBySeeker(seeker));
    }

    // =====================================================
    // 🔹 DASHBOARD COUNTS
    // =====================================================
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(Authentication authentication) {

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        Map<String, Object> data = new HashMap<>();

        data.put("totalApplications", applicationRepository.countBySeeker(seeker));
        data.put("pending", applicationRepository.countBySeekerAndStatus(seeker, "PENDING"));
        data.put("shortlisted", applicationRepository.countBySeekerAndStatus(seeker, "SHORTLISTED"));
        data.put("rejected", applicationRepository.countBySeekerAndStatus(seeker, "REJECTED"));

        return ResponseEntity.ok(data);
    }

    // =====================================================
    // 🔹 CHECK IF ALREADY APPLIED
    // =====================================================
    @GetMapping("/applied/{jobId}")
    public ResponseEntity<?> checkIfApplied(@PathVariable Long jobId,
                                            Authentication authentication) {

        String email = authentication.getName();
        User seeker = userRepository.findByEmail(email).orElseThrow();

        Job job = jobRepository.findById(jobId).orElseThrow();

        boolean applied = applicationRepository.findByJobAndSeeker(job, seeker).isPresent();

        return ResponseEntity.ok(Map.of("applied", applied));
    }
}