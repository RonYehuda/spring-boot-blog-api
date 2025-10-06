package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{
    List<User>findByAge(int age);
    List<User>findByAgeGreaterThan(int age);
    Optional<User> findByEmail(String email);
}
