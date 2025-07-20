package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.ExpensePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpensePayerRepository extends JpaRepository<ExpensePayer, Long> {
    @Query("SELECT ep FROM ExpensePayer ep WHERE ep.expense.id IN :ids")
    List<ExpensePayer> findByExpenseIds(@Param("ids") List<Long> expenseIds);
    
    @Query("SELECT ep FROM ExpensePayer ep WHERE ep.expense.group.id = :groupId")
    List<ExpensePayer> findByGroupId(@Param("groupId") Long groupId);
}
