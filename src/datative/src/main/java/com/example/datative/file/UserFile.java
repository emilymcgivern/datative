package com.example.datative.file;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class UserFile {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Integer id;

  public String name;

  public Long userId;
  
  public String tableName;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  public String getTableName() {
	    return tableName;
	  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}