package com.manjula.jobportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.manjula.jobportal.model.Job;
import com.manjula.jobportal.model.User;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedBy(User recruiter);

    long countByPostedBy(User recruiter);
    long countByPostedByAndActiveTrue(User recruiter);

    List<Job> findTop5ByPostedByOrderByIdDesc(User recruiter);

    List<Job> findByActiveTrue();

    void deleteByPostedBy(User recruiter);

    // ✅ ADD THIS
    List<Job> findAllByOrderByIdDesc();
    
}