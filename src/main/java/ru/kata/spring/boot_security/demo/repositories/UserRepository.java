package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);// новое поле поиск по емаилу

}