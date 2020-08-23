package com.example.datative.dashboard;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.datative.file.FileService;
import com.example.datative.security.model.User;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import scala.Tuple2;

@Service
public class DashboardService {
    
    @Autowired
    private FileService fileService;
    
	
	public static SparkSession getSparkSession() {
		//Setting up single point of entry to spark functionality
		SparkSession spark = SparkSession
		  .builder()
		  .appName("Datative")
		  .master("local")
		  .config("spark.sql.warehouse.dir", "src/main/resources/spark-warehouse")
		  .getOrCreate();
		return spark;
	}
	
	public List<Map<String, String>> getDataSet(String tableName) {
		SparkSession spark = getSparkSession();
		String tableOption = "datative." + tableName; 
		Map<String, String> options = new HashMap<>(); //Creating HashMap of options to be passed to the spark session to build the DataFrame
		options.put("url", "jdbc:mysql:///datative?cloudSqlInstance=datative:europe-west1:datative&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=datative");
		options.put("dbtable", tableOption);
		Dataset<Row> df = spark.read().format("jdbc").options(options).load().cache(); //Creating dataframe from current table
		df.createOrReplaceTempView("tempTable"); //Creates a new temporary view using a dataframe
		Tuple2<String, String>[] columnTypes = df.dtypes(); //Creating tuple of column name and column type to be added to model
		List<Map<String, String>> columnTypePairs = new ArrayList<Map<String, String>>();
		Map<String, String> map1 = new HashMap<String, String>();
		
		//Adding tuple items to a map which will be added to a list 
		for (Tuple2<String, String> item : columnTypes) {
			map1.put(item._1(), item._2());
		}
		
		columnTypePairs.add(map1);
		
		return columnTypePairs;
		
	}
	
	public String getUserBucket() {
		User userDetails = fileService.getAuthenticatedUser();
		Long userID = userDetails.getId();
		String pdfBucket = userID.toString() + userDetails.getFirstName().toLowerCase() + userDetails.getLastName().toLowerCase() + "pdf";
    	return pdfBucket;
	}
		
	public void deleteTable(String tableName) throws SQLException {
    	Connection con = FileService.createDbConnection();
		Statement statement = con.createStatement();
		statement.addBatch("drop table " + tableName);
	    statement.addBatch("DELETE FROM user_file WHERE table_name = '" + tableName + "';");
	    statement.executeBatch();
	}
	
	public void deletePdf(String pdfName) {
		String pdfBucket = getUserBucket();
		String[] pdfStr = pdfName.split(","); //Getting name of file without media link
		Storage storage = StorageOptions.getDefaultInstance().getService();
	    storage.delete(pdfBucket, pdfStr[0]);
	}
		
	public void uploadPdfToBucket(byte[] pdfBytes, String pdfName) {
		String pdfBucket = getUserBucket();
    	Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket newBucket = storage.get(pdfBucket); 
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String textDate = date.format(formatter);
        String fileName = pdfName + " " + textDate;
        newBucket.create(fileName, pdfBytes , "application/pdf");
        BlobId blobId = BlobId.of(pdfBucket, fileName);
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
	}
	
	public List<String> getUserPdfs() {
		String pdfBucket = getUserBucket();
    	Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket userBucket = storage.get(pdfBucket); 
        Page<Blob> blobs = userBucket.list();
        List<String> userPdfs = new ArrayList<String>(); //List of user pdf names and links
        for (Blob blob : blobs.iterateAll()) {
        	userPdfs.add(blob.getName() + "," + blob.getMediaLink()); //Creating string which contains blob name and blob media link to download pdf client side
        }
        
        return userPdfs;
	}
		
}
