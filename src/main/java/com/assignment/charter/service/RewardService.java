package com.assignment.charter.service;

import com.assignment.charter.domain.CustomerTransactionsRewardByMonth;

import java.util.Map;

public interface RewardService {
    public Map<String, CustomerTransactionsRewardByMonth> getCustomerTransactionsRewards();

}