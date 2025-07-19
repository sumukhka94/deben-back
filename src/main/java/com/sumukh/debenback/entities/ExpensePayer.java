package com.sumukh.debenback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_payers")
public class ExpensePayer {

    @EmbeddedId
    private ExpensePayerId id;

    @ManyToOne
    @MapsId("expenseId")
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

}

