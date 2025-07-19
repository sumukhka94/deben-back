package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g JOIN g.members m where m.id = :userId")
    List<Group> findGroupsForUser(Long userId);
}
