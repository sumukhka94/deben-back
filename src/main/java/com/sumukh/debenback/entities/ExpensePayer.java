package com.sumukh.debenback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_payers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

