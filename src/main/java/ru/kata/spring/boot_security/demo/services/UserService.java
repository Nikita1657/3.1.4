package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User saveUser(User user);

    void updateUser(Long id, User updatedUser);

    void deleteUser(Long id);

    User findByEmail(String email);

    List<Role> getAllRolesForUser();
}