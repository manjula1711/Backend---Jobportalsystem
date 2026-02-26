package com.manjula.jobportal.repository;

import com.manjula.jobportal.model.Application;
import com.manjula.jobportal.model.Job;
import com.manjula.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findBySeeker(User seeker);

    long countByJob_PostedBy(User recruiter);
    long countBySeeker(User seeker);
    long countBySeekerAndStatus(User seeker, String status);

    List<Application> findTop5ByJob_PostedByOrderByIdDesc(User recruiter);

    Optional<Application> findByJobAndSeeker(Job job, User seeker);

    List<Application> findByJob_PostedBy(User recruiter);

    // ✅ DELETE HELPERS (for Admin delete cascade)
    void deleteBySeeker(User seeker);
    void deleteByJob(Job job);
    void deleteByJob_PostedBy(User recruiter);
}