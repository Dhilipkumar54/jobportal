package com.portal.jobportal.service;

import com.portal.jobportal.model.JobSeekerProfile;
import com.portal.jobportal.model.RecruiterProfile;
import com.portal.jobportal.model.Users;
import com.portal.jobportal.repository.JobSeekerRepository;
import com.portal.jobportal.repository.RecruiterProfileRepository;
import com.portal.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository,JobSeekerRepository jobSeekerRepository,RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerRepository=jobSeekerRepository;
        this.recruiterProfileRepository=recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users users){
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users savedUsers = usersRepository.save(users);
         int usersId = users.getUserTypeId().getUserTypeId();
         //Checks based on the id whether is seeker or recruiter
        if(usersId==1){
            recruiterProfileRepository.save(new RecruiterProfile(users));
        }else{
            jobSeekerRepository.save(new JobSeekerProfile(users));
        }

        return savedUsers;
    }

    public Optional<Users> findbyEmail(String email){
        return usersRepository.findByEmail(email);
    }

    public Object getCurrentuserProfile() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if(!(authentication instanceof AnonymousAuthenticationToken)){

          String userName = authentication.getName();
          Users users = usersRepository.findByEmail(userName).orElseThrow(()-> new UsernameNotFoundException("could not find user"));
          int userId = users.getUserId();

          if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
              RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
              return recruiterProfile;
          }
          else{
            JobSeekerProfile jobSeekerProfile =  jobSeekerRepository.findById(userId).orElse(new JobSeekerProfile());
            return jobSeekerProfile;
          }
      }

      return null;
    }

    public Users getuserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String userName = authentication.getName();
            Users users = usersRepository.findByEmail(userName).orElseThrow(()-> new UsernameNotFoundException("could not find user"));
            return users;
        }
        return null;
    }
}
