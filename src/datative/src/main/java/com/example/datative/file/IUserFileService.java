package com.example.datative.file;

import java.util.List;

import com.example.datative.file.UserFile;

public interface IUserFileService {
    List<UserFile> findAll();
    List<UserFile> findByuserId(Long id);
}