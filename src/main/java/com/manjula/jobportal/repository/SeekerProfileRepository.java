package com.manjula.jobportal.repository;

import com.manjula.jobportal.model.SeekerProfile;
import com.manjula.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long> {
    Optional<SeekerProfile> findByUser(User user);
    

    // ✅ ADD THIS (for Admin delete)
    void deleteByUser(User user);
}