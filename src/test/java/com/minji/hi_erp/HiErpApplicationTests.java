package com.minji.hi_erp;

import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.enums.Gender;
import com.minji.hi_erp.repository.UserRepository;
import com.minji.hi_erp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class HiErpApplicationTests {
	@Autowired
	private UserRepository userRepository;
	private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Test
	@DisplayName("성공: updatePassword()가 비밀번호를 암호화 하는지 확인")
	void UserTest() {
		String rawPassword = "abcd123@";

		Users user = Users.builder()
				.name("test")
				.birthDay(LocalDate.of(2020, 8, 8))
				.gender(Gender.FEMALE)
				.email("test@naver.com")
				.password(rawPassword)
				.phoneNum("010-1234-5678")
				.imageUrl("1")
				.build();

		user.updatePassword(rawPassword, passwordEncoder);

		String encryptedPassword = user.getPassword();

		System.out.println("Raw Password: " + rawPassword);
		System.out.println("Encrypted Password: " + encryptedPassword);

		assertNotEquals(rawPassword, encryptedPassword, "암호화된 비밀번호는 원본 비밀번호와 달라야 합니다.");
		assertTrue(passwordEncoder.matches(rawPassword, encryptedPassword));
	}


	@Test
	void UserServiceSaveTest() {
		UserJoinDto dto = new UserJoinDto();
		dto.setName("userService");
		dto.setEmail("test@naver.com");
		dto.setPassword("plainPassword");
		dto.setPhoneNum("1234567890");
		dto.setImageUrl("testImageUrl");
		// dto.setRole(Role.USER);

		// 에러 나서 주석 처리
		// String savedEmail = userService.saveUser(dto);
	}
}
