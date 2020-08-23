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

import com.example.datative.security.model.User;
import com.example.datative.security.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class UserEntityTests {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	private User testUser;
	
	@Before
	public void setUp() {
		testUser = new User();
		testUser.setFirstName("Tester");
		testUser.setLastName("Account");
		testUser.setEmail("testeraccount@mail.com");
		testUser.setPassword("testpassword");
	}
	
	
	@Test //Checking the getter returns correct first name
	public void PassIfCorrectFirstNameReturned() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertThat(user.getFirstName()).isEqualTo("Tester");
	}
	
	@Test //Checking the getter returns correct last name
	public void PassIfCorrectLastNameReturned() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertThat(user.getLastName()).isEqualTo("Account");
	}
	
	@Test //Checking the getter returns correct table name
	public void PassIfCorrectEmailReturned() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertThat(user.getEmail()).isEqualTo("testeraccount@mail.com");
	}
	
	@Test //Checking the getter returns correct table name
	public void PassIfCorrectPasswordReturned() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertThat(user.getPassword()).isEqualTo("testpassword");
	}
		
	@Test //Checking that incorrect first name does not match
	public void PassIfFirstNamesDoNotMatch() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertNotEquals(user.getFirstName(), "wrongFirstName");
	}
	
	@Test //Checking that incorrect last name does not match
	public void PassIfLastNamesDoNotMatch() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertNotEquals(user.getLastName(), "wrongLastName");
	}
	
	@Test //Checking that incorrect email does not match
	public void PassIfEmailDoNotMatch() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertNotEquals(user.getEmail(), "wrongEmail");
	}
	
	@Test //Checking that incorrect password does not match
	public void PassIfPasswordsDoNotMatch() {
		User user = this.entityManager.persistAndFlush(testUser);
		assertNotEquals(user.getPassword(), "wrongPassword");
	}
	
	@Test //Checking the correct user is returned by email
	public void checkCorrectUserReturnedByEmail_PassIfEqual() {
		User user = this.entityManager.persistAndFlush(testUser);
		User result = userRepository.findByEmail("testeraccount@mail.com");
		assertThat(user.getFirstName()).isEqualTo(result.getFirstName());
	}
	
	
}
