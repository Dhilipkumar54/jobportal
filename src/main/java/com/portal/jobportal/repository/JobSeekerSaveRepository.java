package com.portal.jobportal.repository;

import com.portal.jobportal.model.JobPostActivity;
import com.portal.jobportal.model.JobSeekerProfile;
import com.portal.jobportal.model.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

}
