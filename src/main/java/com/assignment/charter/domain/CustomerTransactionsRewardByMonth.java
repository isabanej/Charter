package com.assignment.charter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTransactionsRewardByMonth {
    private String month;
    private List<CustomerTransactions> customerTransactions;
    private double totalRewardPerMonth;
}
