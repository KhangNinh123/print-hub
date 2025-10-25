//package com.iuh.printshop.printshop_be.config;
//
//import com.iuh.printshop.printshop_be.entity.Role;
//import com.iuh.printshop.printshop_be.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor // Lombok tự động tạo constructor cho final fields
//public class DataInitializer implements CommandLineRunner {
//
//    private final RoleRepository roleRepository; // Biến final cần được inject
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create default roles if they don't exist
//        if (!roleRepository.existsByName("ROLE_ADMIN")) {
//            roleRepository.save(new Role("ROLE_ADMIN"));
//        }
//
//        if (!roleRepository.existsByName("ROLE_CUSTOMER")) {
//            roleRepository.save(new Role("ROLE_CUSTOMER"));
//        }
//    }
//}

package com.iuh.printshop.printshop_be.config;

import com.iuh.printshop.printshop_be.entity.Role;
import com.iuh.printshop.printshop_be.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default roles if they don't exist
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        if (!roleRepository.existsByName("ROLE_CUSTOMER")) {
            roleRepository.save(new Role("ROLE_CUSTOMER"));
        }
    }
}