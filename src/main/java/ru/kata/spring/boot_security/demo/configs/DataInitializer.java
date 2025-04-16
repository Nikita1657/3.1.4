package ru.kata.spring.boot_security.demo.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;


import java.util.Set;

@Component
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        createRoles();
        createAdmin();
        createUser();
    }

    private void createRoles() {
        if (!roleService.existsByName("ROLE_ADMIN")) {
            roleService.save(new Role("ROLE_ADMIN"));
        }
        if (!roleService.existsByName("ROLE_USER")) {
            roleService.save(new Role("ROLE_USER"));
        }
    }

    private void createAdmin() {
        if (userService.findByEmail("admin@example.com") == null) {
            User admin = new User();
            admin.setFirstName("admin");
            admin.setLastName("admin");  // добавил новые поля как в задании
            admin.setEmail("admin@example.com"); // поменял аунтификацию на емаил
            admin.setPassword("admin"); // Оригинальный пароль
            admin.setRoles(Set.of(
                    roleService.findByName("ROLE_ADMIN"),
                    roleService.findByName("ROLE_USER")
            ));
            userService.saveUser(admin);
        }
    }

    private void createUser() {
        if (userService.findByEmail("user@example.com") == null) {
            User user = new User();
            user.setFirstName("user");
            user.setLastName("user");// добавил новые поля как в задании
            user.setEmail("user@example.com");// поменял аунтификацию на емаил
            user.setPassword("user"); // Оригинальный пароль
            user.setRoles(Set.of(
                    roleService.findByName("ROLE_USER")
            ));
            userService.saveUser(user);
        }
    }
}