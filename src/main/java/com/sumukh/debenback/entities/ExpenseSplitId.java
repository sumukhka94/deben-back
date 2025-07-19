package com.sumukh.debenback.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExpenseSplitId implements Serializable {

    private Long expenseId;
    private Long userId;

}

