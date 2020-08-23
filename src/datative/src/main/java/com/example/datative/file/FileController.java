package com.example.datative.file;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.datative.dashboard.DashboardService;
import com.example.datative.security.model.User;

@Controller    
@SessionAttributes("columnNames")
public class FileController {

    @Autowired
    FileService fileService;
   
    @Autowired
    UserFileRepository userFileRepository;
    
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/upload")
    public String upload() {
        return "upload"; 
    }
    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirs, HttpServletRequest request) throws SQLException {
    	if (file.isEmpty()) { //Validating file to ensure it is not empty
    		redirs.addFlashAttribute("message", "File is empty, please select another file to upload");
            return "redirect:upload";
        }
    	
   	 	String[] name = request.getParameterValues("tableName");
   	 	
        //Checking if table name exists already in DB
        ResultSet results = fileService.checkForTable(name[0]);
        			         		
        if (results.next() == true) {
        	redirs.addFlashAttribute("message", "Table with this name already exists, please choose another!");
            return "redirect:upload";
        }
   	 	
   	 	String tableName = fileService.uploadFile(file, name[0]);

        
    	List<Map<String, String>> columnNames = dashboardService.getDataSet(tableName);
     	User userDetails = fileService.getAuthenticatedUser();
     	redirs.addFlashAttribute("currUser", userDetails);
    	redirs.addFlashAttribute("columnNames", columnNames);
    	redirs.addFlashAttribute("tableName", tableName);

    	return "redirect:/dashboard";
    }
   
}
