package com.sumukh.debenback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_splits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSplit {

    @EmbeddedId
    private ExpenseSplitId id;

    @ManyToOne
    @MapsId("expenseId")
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount_owed", nullable = false)
    private Double amountOwed;

    // Getters & setters
}

