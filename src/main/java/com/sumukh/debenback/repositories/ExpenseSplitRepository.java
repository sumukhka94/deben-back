package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    @Query("SELECT es FROM ExpenseSplit es WHERE es.expense.id IN :ids")
    List<ExpenseSplit> findSplitsByExpenseIds(@Param("ids") List<Long> expenseIds);
}
