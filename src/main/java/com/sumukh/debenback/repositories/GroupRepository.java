package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g JOIN g.members m where m.id = :userId")
    List<Group> findGroupsForUser(Long userId);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.members WHERE g.id = :groupId")
    Optional<Group> findByIdWithMembers(@Param("groupId") Long groupId);

    @Query(value = """
SELECT u.id AS user_id,
       u.name AS name,
       COALESCE(SUM(p.amount_paid),0) AS total_paid,
       COALESCE(SUM(s.amount_owed),0) AS total_share
FROM group_members gm
JOIN users u ON u.id = gm.user_id
LEFT JOIN expenses e ON e.group_id = gm.group_id
LEFT JOIN expense_payers p
       ON p.expense_id = e.id AND p.user_id = u.id
LEFT JOIN expense_splits s
       ON s.expense_id = e.id AND s.user_id = u.id
WHERE gm.group_id = :groupId
GROUP BY u.id, u.name
""", nativeQuery = true)
    List<Object[]> computeMemberBalances(@Param("groupId") Long groupId);
}
