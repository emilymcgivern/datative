package com.example.datative.file;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jooq.CreateTableAsStep;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.datative.cleaning.DataCleaning;
import com.example.datative.security.model.User;
import com.example.datative.security.repository.UserRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.paging.Page;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.*;
import com.google.api.services.sqladmin.model.ImportContext.CsvImportOptions;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

@Service
public class FileService {

	@Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    UserFileRepository userFileRepository;
    
    @Autowired
    DataCleaning dataCleaning;
   
    

    public String uploadFile(MultipartFile file, String userTableName) { 
    	List<String> acceptedExtensions = Arrays.asList("csv", "xls", "xlsx", "txt");
    	String extension = FilenameUtils.getExtension(file.getOriginalFilename()); //Getting MultipartFileobject extension
    	
		if (!acceptedExtensions.contains(extension)) { //Throw exception if file is not of accepted type
		 	throw new FileTypeException("Cannot upload file of type ." + extension + ", please try again!");
	 	}
	
    	try {
         	 String originalFileName = file.getOriginalFilename();
             Path copyLocation = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(originalFileName));
             Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
             InputStream fileInputStream = file.getInputStream(); //Creating stream from uploaded file object
             String tableName = addFileToBucket(userTableName, originalFileName, fileInputStream);
             
             return tableName;
           
          
    	 } catch (Exception e) {
             e.printStackTrace();
             throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                 + ". Please try again!");
         }       
 
    }
 
    public User getAuthenticatedUser() { 
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
    	String currentPrincipalName = authentication.getName();
    	User userDetails = userRepository.findByEmail(currentPrincipalName); //Getting current authenticated users details
    	return userDetails;
    }

	public String addFileToBucket(String userTableName, String originalFileName, InputStream fileInputStream) throws IOException, SQLException, GeneralSecurityException, InterruptedException {
    	User userDetails = getAuthenticatedUser();
    	Long userID = userDetails.getId();
    	String uniqueBucket = userID.toString() + userDetails.getFirstName().toLowerCase() + userDetails.getLastName().toLowerCase();
           	
    	//Checking if bucket exists already
    	Storage storage = StorageOptions.newBuilder()
    	        .setCredentials(ServiceAccountCredentials.fromStream(getClass().getResourceAsStream("/key.json")))
    	        .setProjectId("datative")
    	        .build()
    	        .getService();
    	
    	checkForBucket(uniqueBucket, storage);
    
    	//Saving file to bucket
    	LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String textDate = date.format(formatter);
        Bucket newBucket = storage.get(uniqueBucket);
        String fileName = originalFileName + " " + textDate;
        Blob blob = newBucket.create(fileName, fileInputStream , "application/pdf");
        BlobId blobId = BlobId.of(uniqueBucket, fileName);
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        
        String tableName = createNewTable(userTableName, newBucket, blob, fileName, originalFileName, uniqueBucket, storage, userID);
        
        return tableName;
    }
	
	public String createNewTable(String userTableName, Bucket newBucket, Blob blob, String fileName, String originalFileName, String uniqueBucket, Storage storage, Long userID) throws IOException, SQLException, GeneralSecurityException, InterruptedException {
		ReadChannel readChannel = blob.reader(); //Reading file from bucket and into tablesaw, data cleaning goes here
		InputStream stream = Channels.newInputStream(readChannel);
		DateTimeFormatter formatter = DataCleaning.getFormatter();		
	    Table oldDataSet = Table.read().csv(CsvReadOptions.builder(stream).dateFormat(formatter)); //Original table object which has not been cleaned.
		Table dataSet = DataCleaning.cleanData(oldDataSet); //New table object which has been cleaned
		List<String> columnNames = dataSet.columnNames();
		ArrayList<DataType<?>> typesList = getDataTypes(dataSet);
		createCleanedFile(fileName, dataSet, newBucket, uniqueBucket, storage); //Creating copy of cleaned CSV in tmp storage and then uploading it to the user bucket so that tables can be created from this
		Connection con = createDbConnection(); //Connection to Cloud SQL instance to read data from DB
        String tableName = userTableName + userID.toString(); //Creating table name based on user input        
        addDataToTable(con, tableName, columnNames, uniqueBucket, originalFileName, userID, fileName, typesList);
        
        return tableName;
	}
	
	public void addDataToTable(Connection con, String tableName, List<String> columnNames, String uniqueBucket, String originalFileName, Long userID, String fileName, ArrayList<DataType<?>> typesList) throws IOException, GeneralSecurityException, SQLException, InterruptedException {
		DSLContext create = DSL.using(con, SQLDialect.MYSQL);
        CreateTableAsStep<Record> table = create.createTableIfNotExists(tableName); //Creating table in database
		CreateTableColumnStep column = ((CreateTableColumnStep) table).column(columnNames.get(0), typesList.get(0)); //Adding first column to table
    	column.execute();
    	
        for (int i = 1; i < columnNames.size(); i++) { 
        	create.alterTable(tableName).add(columnNames.get(i), typesList.get(i)).execute(); //Iteratively adding columns to table
        }
     
    	//Data model class that parses into JSON to be transmitted over HTTP 
        String uri = "gs://" + uniqueBucket + "/" + fileName + " cleaned";
		InstancesImportRequest requestBody = new InstancesImportRequest();
        ImportContext ic = new ImportContext();
        ic.setKind("sql#importContext");
        ic.setFileType("csv");
        ic.setUri(uri);
        ic.setDatabase("datative");      
        CsvImportOptions csv = new CsvImportOptions();
        csv.setTable(tableName);
        ic.setCsvImportOptions(csv); 
        requestBody.setImportContext(ic);
        
        SQLAdmin sqlAdminService = createSqlAdminService();
        SQLAdmin.Instances.SQLAdminImport request = sqlAdminService.instances().sqladminImport("datative", "datative", requestBody);
        request.execute(); //Executing request which populates the table with the corresponding data
        Thread.sleep(5000);
        
        deleteHeaderRow(tableName, con);
        saveUserFileDetails(originalFileName, userID, tableName); 
	}
	
	public static void createCleanedFile(String fileName, Table dataSet, Bucket newBucket , String uniqueBucket, Storage storage) throws IOException {
		File temp = File.createTempFile(fileName + "new", ".csv"); 
		String p = temp.getAbsolutePath(); 
		dataSet.write().csv(p);
		byte[] bFile = Files.readAllBytes(Paths.get(p));
		newBucket.create(fileName + " cleaned", bFile , "application/pdf");
        BlobId cleanedBlobId = BlobId.of(uniqueBucket, fileName + " cleaned"); //Adding blob of cleaned file to user bucket
        storage.createAcl(cleanedBlobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
	}
	
	public static Connection createDbConnection() throws SQLException {
		String instanceConnectionName = "datative:europe-west1:datative";
        String databaseName = "datative";
        String IP_of_instance = "35.241.139.255";
        String username = "root";
        String password = "datative";
        String jdbcUrl = String.format(
            "jdbc:mysql://%s/%s?cloudSqlInstance=%s&allowMultiQueries=true"
            	+ "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",

        IP_of_instance,
            databaseName,
            instanceConnectionName);

        Connection con = DriverManager.getConnection(jdbcUrl, username, password);
        
        return con;
	}
    
    public ResultSet checkForTable(String userTableName) throws SQLException {
    	//Checking if table name already exists
    	Connection con = createDbConnection();
    	User userDetails = getAuthenticatedUser();
    	Long userID = userDetails.getId();
    	String tableName = userTableName + userID.toString(); 
        String tableCheck = "SELECT * from user_file where table_name = '" + tableName + "';";
        Statement exe = con.createStatement(); 
        ResultSet results = exe.executeQuery(tableCheck);
        
        return results;
    }
    
    public static void deleteHeaderRow(String tableName, Connection con) throws SQLException {
    	String deleteStatement = "DELETE FROM `" + tableName + "` LIMIT 1"; //Deleting first row from the table
        PreparedStatement stm = con.prepareStatement(deleteStatement);
        stm.executeUpdate();
    }
    
    public static void checkForBucket(String uniqueBucket, Storage storage) {
    	//Check if a bucket exists already, if not it creates a new one
	    Page<Bucket> buckets = storage.list();
		ArrayList<String> bList = new ArrayList<String>();
	    
	    for (Bucket bucket : buckets.iterateAll()) {
    	  String bName = bucket.getName();  
    	  bList.add(bName);
    	}  
	    
	    if (!bList.contains(uniqueBucket)) {
	    	storage.create(BucketInfo.newBuilder(uniqueBucket).setStorageClass(StorageClass.STANDARD).setLocation("eu").build());
	    	storage.create(BucketInfo.newBuilder(uniqueBucket + "pdf").setStorageClass(StorageClass.STANDARD).setLocation("eu").build()); //Creating bucket for storing PDF reports
	    }
    }
   
    
    public static ArrayList<DataType<?>> getDataTypes(Table dataSet) {
		ColumnType[] columnTypes = dataSet.columnTypes(); 
		
		Map<String, DataType<?>> choices = new LinkedHashMap<>();
		choices.put("STRING", SQLDataType.VARCHAR);
		choices.put("LONG", SQLDataType.INTEGER);
		choices.put("INTEGER", SQLDataType.INTEGER);
		choices.put("DOUBLE", SQLDataType.DOUBLE);
		choices.put("LOCAL_DATE", SQLDataType.LOCALDATE);
		choices.put("LOCAL_TIME", SQLDataType.LOCALTIME);
		ArrayList<DataType<?>> typesList = new ArrayList<DataType<?>>(); 
		
		for (ColumnType c : columnTypes) {
			String s  = c.toString();
			DataType<?> t = choices.get(s);
			typesList.add(t);
		}
		
		return typesList;
    }
    
	public void saveUserFileDetails(String originalFileName, Long userID, String tableName) {
    	UserFile n = new UserFile();
        n.setName(originalFileName);
        n.setUserId(userID);
        n.setTableName(tableName);
        userFileRepository.save(n);   
    }
    
    public static SQLAdmin createSqlAdminService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new SQLAdmin.Builder(httpTransport, jsonFactory, requestInitializer).setApplicationName("datative").build();
      }
    
    public class FileStorageException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private String msg;

        public FileStorageException(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }
    
    public class FileTypeException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private String msg;

        public FileTypeException(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }
    
    @ControllerAdvice
    public class AppExceptionHandler {

        @ExceptionHandler(FileStorageException.class)
        public String handleException(FileStorageException exception, RedirectAttributes redirectAttributes) {
        	
        	String message = exception.getMsg();
        	redirectAttributes.addFlashAttribute("message", message);
            return "redirect:upload"; //Redirecting back to upload and flashing failure message
        }
        
        @ExceptionHandler(FileTypeException.class)
        public String handleFileTypeException(FileTypeException exception, RedirectAttributes redirectAttributes) {

        	String message = exception.getMsg();
        	redirectAttributes.addFlashAttribute("message", message);
            return "redirect:upload"; //Redirecting back to upload and flashing failure message
        }
        
        
    }
}
