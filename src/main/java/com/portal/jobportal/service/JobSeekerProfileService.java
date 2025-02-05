package com.portal.jobportal.service;

import com.portal.jobportal.model.JobSeekerProfile;
import com.portal.jobportal.repository.JobSeekerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerRepository jobSeekerProfileRepository;

    public JobSeekerProfileService(JobSeekerRepository jobSeekerProfileRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }

    public Optional<JobSeekerProfile> getOne(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }
}
