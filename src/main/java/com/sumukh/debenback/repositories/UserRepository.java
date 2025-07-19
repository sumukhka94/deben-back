package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
