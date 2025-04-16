package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entities.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    List<Role> getAllRoles();

    void save(Role role);

    Role findByName(String name);

    boolean existsByName(String name);

    Set<Role> getRolesByIds(Set<Long> roleIds); // Новый метод
}