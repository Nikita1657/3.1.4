package ru.kata.spring.boot_security.demo.services;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;
@Slf4j
@Service
public class UserServiceImpl implements UserService , UserDetailsService  {
    // работа только с репозиториями
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user; // User должен реализовывать UserDetails
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        // Кодируем пароль перед сохранением
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // Убедимся, что роли установлены
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, User updatedUser) { // переделал метод
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ;

        // Обновляем только те поля, которые были изменены
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            // Хешируем новый пароль
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
// Обновляем роли, если они были переданы
        if (updatedUser.getRoleIds() != null && !updatedUser.getRoleIds().isEmpty()) {
            Set<Role> rolesSet = new HashSet<>(roleRepository.findAllById(updatedUser.getRoleIds()));
            existingUser.setRoles(rolesSet);
            userRepository.save(existingUser);
        }
    }
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Role> getAllRolesForUser() {
        return roleRepository.findAll();
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    private void printUserDetails(User user, String encodedPassword) {
        // добавил лог
        log.debug(""" 
        User Details:
        ID: {}
        First Name: {}
        Last Name: {}
        Email: {}""",
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());

        // Логируем только факт изменения пароля без деталей
        log.debug("Password was updated");
    }
}
