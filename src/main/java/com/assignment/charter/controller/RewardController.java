package com.assignment.charter.controller;

import com.assignment.charter.domain.CustomerTransactionsRewardByMonth;
import com.assignment.charter.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/customer/rewards")
public class RewardController {
    @Autowired
    private RewardService rewardService;

    @GetMapping
    public Map<String, CustomerTransactionsRewardByMonth> getRewardsByTransaction() {
        return rewardService.getCustomerTransactionsRewards();
    }

}
