package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@SpringBootApplication
public class JwtSpringboot3Application {

    public static void main(String[] args) {
        SpringApplication.run(JwtSpringboot3Application.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed tai khoan sinh vien Nguyen Minh Tri
            if (userRepository.findByEmail("24110359@student.hcmute.edu.vn").isEmpty()) {
                User student = new User();
                student.setFullName("Nguyễn Minh Trí");
                student.setEmail("24110359@student.hcmute.edu.vn");
                student.setPassword(passwordEncoder.encode("123456"));
                student.setImages("/images/avatar.png");
                userRepository.save(student);
            }

            // Seed tai khoan giang vien nhu trong slide de de dang cham bai
            if (userRepository.findByEmail("trungnh@hcmute.edu.vn").isEmpty()) {
                User teacher = new User();
                teacher.setFullName("Nguyễn Hữu Trung");
                teacher.setEmail("trungnh@hcmute.edu.vn");
                teacher.setPassword(passwordEncoder.encode("123456"));
                teacher.setImages("/images/avatar.png");
                userRepository.save(teacher);
            }
        };
    }
}
