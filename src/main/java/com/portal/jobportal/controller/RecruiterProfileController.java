package com.portal.jobportal.controller;

import com.portal.jobportal.model.RecruiterProfile;
import com.portal.jobportal.model.Users;
import com.portal.jobportal.repository.UsersRepository;
import com.portal.jobportal.service.RecruiterProfileService;
import com.portal.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository userRepo;

    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public RecruiterProfileController(UsersRepository userRepo,RecruiterProfileService recruiterProfileService) {
        this.userRepo = userRepo;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken)){
            String username = auth.getName();
            Users user = userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(user.getUserId());

            if(recruiterProfile.isPresent()){
                model.addAttribute("profile",recruiterProfile.get());
            }

        }
        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image")MultipartFile multipartFile, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String userName = authentication.getName();
            Users user = userRepo.findByEmail(userName).orElseThrow(()->new UsernameNotFoundException("could not find user"));
            recruiterProfile.setUserId(user);
            recruiterProfile.setUserAccountId(user.getUserId());
        }
        model.addAttribute("profile",recruiterProfile);
        String fileName = "";

        if(!multipartFile.getOriginalFilename().equals("")){
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedProfile = recruiterProfileService.addNew(recruiterProfile);
        String uplodadDir = "photos/recruiter/"+ savedProfile.getUserAccountId();

        try{
            FileUploadUtil.saveFile(uplodadDir,fileName,multipartFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:/dashboard";
    }
}
