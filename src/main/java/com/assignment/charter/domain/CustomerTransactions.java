package com.assignment.charter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTransactions {
    private Customer customer;
    private Set<Transaction> transactionSet;
    private double customerRewardsTotalPerMonth;
}
