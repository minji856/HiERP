package com.minji.hi_erp;

import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import com.minji.hi_erp.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.util.AssertionErrors.assertNotEquals;

@SpringBootTest
class HiErpApplicationTests {
	@Autowired
	private UserRepository userRepository;
	private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Test
	void UserTest() {
		Users savedUser = userRepository.save(new Users("test","test@naver.com","abcd123@","1","1"));
		String rawPassword = savedUser.getPassword(); // 저장된 원본 비밀번호
		String encryptedPassword = passwordEncoder.encode(rawPassword); // 암호화된 비밀번호

		System.out.println("Raw Password: " + rawPassword);
		System.out.println("Encrypted Password: " + encryptedPassword);
		System.out.println(userRepository.findAll() + "이름 : " + savedUser.getName());

		assertNotEquals(rawPassword, encryptedPassword, "암호화된 비밀번호는 원본 비밀번호와 달라야 합니다.");
	}

	@Test
	void UserServiceSaveTest() {
		UserJoinDto dto = new UserJoinDto();
		dto.setName("userService");
		dto.setEmail("test@naver.com");
		dto.setPassword("plainPassword");
		dto.setPhoneNum("1234567890");
		dto.setImageUrl("testImageUrl");

		String userId = userService.saveUser(dto);
	}
}
