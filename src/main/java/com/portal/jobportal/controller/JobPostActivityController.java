package com.portal.jobportal.controller;

import com.portal.jobportal.model.JobPostActivity;
import com.portal.jobportal.model.RecruiterJobsDto;
import com.portal.jobportal.model.RecruiterProfile;
import com.portal.jobportal.model.Users;
import com.portal.jobportal.repository.JobPostActivityRepository;
import com.portal.jobportal.service.JobPostActivityService;
import com.portal.jobportal.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService) {
        this.usersService = usersService;
        this.jobPostActivityService=jobPostActivityService;
    }

    @GetMapping("/dashboard")
    public String searchJobs(Model model){
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
        return "redirect:/dashboard";
    }
    @PostMapping("/dashboard/edit/{id}")
    public String editSource(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentuserProfile());

        return "add-jobs";
    }


}
