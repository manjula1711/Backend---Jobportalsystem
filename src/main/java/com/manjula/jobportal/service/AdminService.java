package com.manjula.jobportal.service;

import com.manjula.jobportal.model.Job;
import com.manjula.jobportal.model.Role;
import com.manjula.jobportal.model.User;
import com.manjula.jobportal.repository.ApplicationRepository;
import com.manjula.jobportal.repository.JobRepository;
import com.manjula.jobportal.repository.RecruiterProfileRepository;
import com.manjula.jobportal.repository.SeekerProfileRepository;
import com.manjula.jobportal.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SeekerProfileRepository seekerProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    // ===============================
    // 📊 Dashboard Counts
    // ===============================
    public Map<String, Object> getDashboardCounts() {

        Map<String, Object> data = new HashMap<>();

        data.put("totalUsers", userRepository.count());
        data.put("totalSeekers", userRepository.countByRole(Role.SEEKER));
        data.put("totalRecruiters", userRepository.countByRole(Role.RECRUITER));
        data.put("totalJobs", jobRepository.count());
        data.put("totalApplications", applicationRepository.count());

        return data;
    }

    // ===============================
    // 👥 Manage Users
    // ===============================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===============================
    // ❌ Delete User Safely
    // ===============================
    @Transactional
    public String deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent deleting ADMIN
        if (user.getRole() == Role.ADMIN) {
            return "Cannot delete ADMIN user";
        }

        // If SEEKER
        if (user.getRole() == Role.SEEKER) {

            // Delete applications first
            applicationRepository.deleteBySeeker(user);

            // Delete seeker profile
            seekerProfileRepository.deleteByUser(user);

            // Delete user
            userRepository.delete(user);

            return "Seeker deleted successfully";
        }

        // If RECRUITER
        if (user.getRole() == Role.RECRUITER) {

            // Delete applications of recruiter's jobs
            applicationRepository.deleteByJob_PostedBy(user);

            // Delete recruiter's jobs
            jobRepository.deleteByPostedBy(user);

            // Delete recruiter profile
            recruiterProfileRepository.deleteByUser(user);

            // Delete user
            userRepository.delete(user);

            return "Recruiter deleted successfully";
        }

        // Fallback
        userRepository.delete(user);
        return "User deleted successfully";
    }

    // ===============================
    // 💼 Manage Jobs
    // ===============================
    public List<Job> getAllJobs() {
        return jobRepository.findAllByOrderByIdDesc();
    }

    // ===============================
    // ❌ Delete Job Safely
    // ===============================
    @Transactional
    public String deleteJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Delete applications first
        applicationRepository.deleteByJob(job);

        // Delete job
        jobRepository.delete(job);

        return "Job deleted successfully";
    }
}