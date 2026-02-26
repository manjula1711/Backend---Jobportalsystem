package com.manjula.jobportal.repository;

import com.manjula.jobportal.model.RecruiterProfile;
import com.manjula.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    Optional<RecruiterProfile> findByUser(User user);
    
 // ✅ ADD THIS (for Admin delete)
    void deleteByUser(User user);
}