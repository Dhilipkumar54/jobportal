package com.portal.jobportal.service;

import com.portal.jobportal.model.UsersType;
import com.portal.jobportal.repository.UsersTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeService {

    private final UsersTypeRepository userTypeRepo;

    public UserTypeService(UsersTypeRepository userTypeRepo){
        this.userTypeRepo=userTypeRepo;
    }

    public List<UsersType> getAll(){
        return userTypeRepo.findAll();
    }
}
