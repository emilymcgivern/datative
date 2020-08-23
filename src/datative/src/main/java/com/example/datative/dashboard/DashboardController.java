package com.example.datative.dashboard;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.datative.file.FileService;
import com.example.datative.file.IUserFileService;
import com.example.datative.file.UserFile;
import com.example.datative.security.model.User;
import com.example.datative.security.repository.UserRepository;

@Controller
public class DashboardController {
	
    @Autowired
    DashboardService dashboardService;
    
    @Autowired
    FileService fileService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IUserFileService userFileService;
    
    @GetMapping("/profile")
    public String profile(Model model) {
    	User userDetails = fileService.getAuthenticatedUser();
    	Long userID = userDetails.getId();
        List<UserFile> ids = userFileService.findByuserId(userID);
        List<String> userPdfs = dashboardService.getUserPdfs();
        model.addAttribute("ids", ids);
        model.addAttribute("pdfs", userPdfs);
        model.addAttribute("currUser", userDetails);
        return "profile"; 
    }
    
    
    @GetMapping("/dashboard")
    public String getDashboard(){
    	return "dashboard";
    }
   
    
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = {"select"})
    public String dashboard(@RequestParam(name = "select") String tableName, Model model) {
    	List<Map<String, String>> columnNames = dashboardService.getDataSet(tableName);
     	User userDetails = fileService.getAuthenticatedUser();
    	model.addAttribute("columnNames", columnNames);
    	model.addAttribute("tableName", tableName);
    	model.addAttribute("currUser", userDetails);
		return "dashboard";
    }
    
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = {"delete"})
    public String delete(@RequestParam(name = "delete") String tableName) throws SQLException {
    	dashboardService.deleteTable(tableName);
		return "redirect:profile";
    }
    
    @RequestMapping(value = "/deletePdf", method = RequestMethod.POST, params = {"delete"})
    public String deletePdf(@RequestParam(name = "delete") String pdfName) {
    	dashboardService.deletePdf(pdfName);
		return "redirect:profile";
    }
      
    
    @PostMapping("/uploadPDF")
    @ResponseBody
    public void uploadPdf(@RequestParam(name = "pdf") String pdf, @RequestParam(name = "pdfName") String pdfName) {
    	String base64Pdf = pdf.split(",")[1]; //Splitting Base64 string to get PDF data and remove metadata
    	byte[] pdfBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Pdf); //Decoding Base64 string to byte array
        dashboardService.uploadPdfToBucket(pdfBytes, pdfName); //Passing byte array to upload function
        
    }
   
}
