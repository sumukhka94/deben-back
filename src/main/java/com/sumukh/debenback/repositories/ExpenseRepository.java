package com.sumukh.debenback.repositories;

import com.sumukh.debenback.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
