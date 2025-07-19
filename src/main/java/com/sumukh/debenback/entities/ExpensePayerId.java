package com.sumukh.debenback.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExpensePayerId implements Serializable {

    private Long expenseId;
    private Long userId;

    // equals & hashCode
}

