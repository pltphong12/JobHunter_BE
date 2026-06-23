package org.example.jobhunter.config;

import org.example.jobhunter.domain.Role;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.repository.RoleRepository;
import org.example.jobhunter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();
        if (countRoles == 0) {
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin có toàn quyền hệ thống");
            adminRole.setActive(true);
            this.roleRepository.save(adminRole);
            Role hrRole = new Role();
            hrRole.setName("HR");
            hrRole.setDescription("Quản lý job và hồ sơ ứng tuyển của công ty");
            hrRole.setActive(true);
            this.roleRepository.save(hrRole);
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Ứng viên tìm việc");
            userRole.setActive(true);
            this.roleRepository.save(userRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(User.GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }
            this.userRepository.save(adminUser);
        }

        if (countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");

        } else {
            System.out.println(">>> END INIT DATABASE");
        }
    }
}
