package com.example.datative.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFileService implements IUserFileService {

    @Autowired
    private UserFileRepository userFileRepo;

    @Override
    public List<UserFile> findAll() {

        return (List<UserFile>) userFileRepo.findAll();
    }
    
    @Override
    public List<UserFile> findByuserId(Long id) {

        return userFileRepo.findByuserId(id);
    }
    
    
}
