package com.example.datative;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.datative.file.FileService;
import com.example.datative.file.FileService.FileStorageException;
import com.example.datative.file.FileService.FileTypeException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UploadTests {
	
	@Autowired
	FileService fileService;
	
	MockMultipartFile multipartPythonFile = new MockMultipartFile("test.py", "test.py", null, "print ('Hello World!')".getBytes());
	MockMultipartFile multipartCFile = new MockMultipartFile("test.c", "test.c", null, "printf('Hello World!')".getBytes());
	MockMultipartFile multipartEmptyFile = new MockMultipartFile("test.csv", "test.csv", "text/csv", "".getBytes());
	String tableName = "SampleTable_Name";
	
	
	@Test(expected = FileTypeException.class) //Checking that correct error is thrown when .py extension file uploaded
	public void shouldThrowExtensionErrorMessagePython() throws Exception {
		fileService.uploadFile(multipartPythonFile, tableName);
	}
	
	@Test(expected = FileTypeException.class) //Checking that correct error is thrown when .c extension file uploaded
	public void shouldThrowExtensionErrorMessageC() throws Exception {
		fileService.uploadFile(multipartCFile, tableName);
	}
	
	@Test(expected = FileStorageException.class) //Checking that correct error is thrown when a file with incorrect format is uploaded
	public void shouldThrowStorageExceptionErrorMessageIncorrectContent() throws Exception {
		MockMultipartFile mockMultipartCsvFile = new MockMultipartFile("test.csv", "test.csv", "multipart/form-data", "test".getBytes());
		fileService.uploadFile(mockMultipartCsvFile, tableName);
	}

	
	
}
