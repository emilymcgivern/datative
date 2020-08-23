package com.example.datative.file;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserFileRepository extends CrudRepository<UserFile, Integer> {

	List<UserFile> findByuserId(Long id);
		
}