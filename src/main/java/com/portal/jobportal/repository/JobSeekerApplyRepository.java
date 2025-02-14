package com.portal.jobportal.repository;

import com.portal.jobportal.model.JobPostActivity;
import com.portal.jobportal.model.JobSeekerApply;
import com.portal.jobportal.model.JobSeekerSave;
import com.portal.jobportal.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}

