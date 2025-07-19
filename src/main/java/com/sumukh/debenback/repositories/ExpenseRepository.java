package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT SUM(e.totalAmount) FROM Expense e WHERE e.group.id = :groupId")
    Double sumByGroupId(Long groupId);
    List<Expense> findByGroupId(Long groupId);

    int countByGroupId(Long groupId);

    List<Expense> findByGroupIdOrderByPaidAtDesc(Long groupId);
}
