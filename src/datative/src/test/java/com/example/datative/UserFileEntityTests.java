package com.example.datative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.datative.file.UserFile;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class UserFileEntityTests {
	
	@Autowired
	private TestEntityManager entityManager;
	
	private UserFile testUserFile;
	
	@Before
	public void setUp() {
		testUserFile = new UserFile();
		testUserFile.setName("testFile");
		testUserFile.setUserId((long) 341);
		testUserFile.setTableName("test_table341");
	}
	
	
	@Test //Checking the getter returns correct name
	public void PassIfCorrectNameReturned() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertThat(file.getName()).isEqualTo("testFile");
	}
	
	@Test //Checking the getter returns correct id
	public void PassIfCorrectUserIdReturned() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertThat(file.getUserId()).isEqualTo(341);
	}
	
	@Test //Checking the getter returns correct table name
	public void PassIfCorrectTableNameReturned() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertThat(file.getTableName()).isEqualTo("test_table341");
	}
	
	@Test //Checking that incorrect name does not match
	public void PassIfNamesDoNotMatch() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertNotEquals(file.getName(), "wrongName");
	}
	
	@Test //Checking that incorrect user id does not match
	public void PassIfUserIdsDoNotMatch() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertNotEquals(file.getUserId(), 1, 0.0);
	}
	
	@Test //Checking that incorrect table name does not match
	public void PassIfTableNamesDoNotMatch() {
		UserFile file = this.entityManager.persistAndFlush(testUserFile);
		assertNotEquals(file.getTableName(), "wrongTableName");
	}
	
}
