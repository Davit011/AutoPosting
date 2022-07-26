package com.example.autoposting.repository;

import com.example.autoposting.model.User;
import com.example.autoposting.model.UserCategory;
import com.example.autoposting.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByProfileId(String id);

    Optional<User> findUserByName(String name);

    List<User> findAllByIsChecked(boolean checked);

    List<User> findAllByCategoryIsNull();

    List<User> findAllByCategory(UserCategory userCategory);

    List<User> findAllByCategoryIsNullAndAndStatus(UserStatus status);

    List<User> findAllByCategoryAndStatus(UserCategory userCategory,UserStatus status);

    List<User> findAllByStatusIsNull();

    List<User> findAllByStatus(UserStatus status);

}
