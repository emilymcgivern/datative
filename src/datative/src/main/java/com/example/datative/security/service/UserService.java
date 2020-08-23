package com.example.datative.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.datative.security.model.User;
import com.example.datative.security.web.dto.UserRegistrationDto;

import java.util.ArrayList;
import java.util.List;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration);


}
