package com.portal.jobportal.repository;

import com.portal.jobportal.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerRepository extends JpaRepository<JobSeekerProfile,Integer> {
}
