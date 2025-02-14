package com.portal.jobportal.controller;

import com.portal.jobportal.model.*;
import com.portal.jobportal.service.JobPostActivityService;
import com.portal.jobportal.service.JobSeekerApplyService;
import com.portal.jobportal.service.JobSeekerSaveService;
import com.portal.jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;

    private final JobSeekerApplyService jobSeekerApplyService;

    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService,
                                     JobSeekerApplyService jobSeekerApplyService,JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobPostActivityService=jobPostActivityService;
        this.jobSeekerApplyService=jobSeekerApplyService;
        this.jobSeekerSaveService=jobSeekerSaveService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model ,
                             @RequestParam(value="job",required = false) String job,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "partTime", required = false) String partTime,
                             @RequestParam(value = "fullTime", required = false) String fullTime,
                             @RequestParam(value = "freelance", required = false) String freelance,
                             @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly", required = false) String officeOnly,
                             @RequestParam(value = "partialRemote", required = false) String partialRemote,
                             @RequestParam(value = "today", required = false) boolean today,
                             @RequestParam(value = "days7", required = false) boolean days7,
                             @RequestParam(value = "days30", required = false) boolean days30

    ){

        model.addAttribute("partTime", Objects.equals(partTime, "partTime"));
        model.addAttribute("fullTime", Objects.equals(partTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(partTime, "Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(partTime, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(partTime, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partTime, "Partial-Remote"));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        //To get the search space from the query param
        LocalDate searchDate = null;

        //a list to add the search results of jobpost
        List<JobPostActivity> jobPost = null;

        //Flag , if no time-space is selected from front end, they show all jobs
        boolean dateSearchFlag = true;
        //Flag , if employment-space is selected from front end, they show all jobs
        boolean remote = true;
        //Flag , if work type is selected from front end, they show all jobs
        boolean type = true;

        //Getting date space
        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        //checking for type of employment
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false;
        }

        //checking for type of mode
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false;
        }

        //If no filters are selected, returns all jobs posted
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll();

        //If any filter selected, returns jobs based on that.
        } else {
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }


        Object currentUserProfile = usersService.getCurrentuserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String userName = authentication.getName();
            model.addAttribute("username",userName);
            //Checks whether the logged in is a recruiter and adds it to the model
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile)currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);
            }
            else{
                List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaveList =jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

                boolean exist;
                boolean saved;

                for (JobPostActivity jobActivity : jobPost) {
                    exist = false;
                    saved = false;
                    //Checks whether he applied for a particular job
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                            jobActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }
                    //Checks whether the job is saved or not
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                            jobActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }

                    if (!exist) {
                        jobActivity.setIsActive(false);
                    }
                    if (!saved) {
                        jobActivity.setIsSaved(false);
                    }

                    model.addAttribute("jobPost", jobPost);
                }
            }
        }
        model.addAttribute("user",currentUserProfile);
        return "dashboard";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model){
        //New job post Activity obj is created and added to the model
        model.addAttribute("jobPostActivity",new JobPostActivity());
        // to know whether he is a recruiter or seeker we use getCurrentUserProfile
        model.addAttribute("user",usersService.getCurrentuserProfile());

        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity,Model model){
        //validates the user
        Users user = usersService.getuserProfile();
        if(user!=null){
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity",jobPostActivity);
        JobPostActivity savedActivity = jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }
    @PostMapping("/dashboard/edit/{id}")
    public String editSource(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentuserProfile());

        return "add-jobs";
    }


}
