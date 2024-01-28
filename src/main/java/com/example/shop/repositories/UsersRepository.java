package com.example.shop.repositories;


import com.example.shop.model.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users, Integer> {

    @Query("SELECT u.password FROM Users u WHERE u.username = :username")
    Optional<String> findPassword(@Param("username") String username);

    @Query("SELECT u FROM Users u WHERE u.username = :username")
    Users getUser(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.username = :username")
    boolean userExist(@Param("username") String username);
}
