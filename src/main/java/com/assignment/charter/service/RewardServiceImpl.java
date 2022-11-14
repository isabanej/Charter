package com.assignment.charter.service;

import com.assignment.charter.dao.CustomerTransactionRepository;
import com.assignment.charter.domain.CustomerTransactions;
import com.assignment.charter.domain.CustomerTransactionsRewardByMonth;
import com.assignment.charter.domain.Transaction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final CustomerTransactionRepository repository;

    @Override
    public Map<String, CustomerTransactionsRewardByMonth> getCustomerTransactionsRewards() {
        Map<String, CustomerTransactions> customerTransactions = repository.getCustomerTransactions();
        Map<String, CustomerTransactionsRewardByMonth> finalResponse = new LinkedHashMap<>();
        List<CustomerTransactions> customerTransactionsList = customerTransactions.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
        for (CustomerTransactions customerTransact : customerTransactionsList) {
            Set<Transaction> transactionSet = customerTransact.getTransactionSet();
            for (Transaction transaction : transactionSet) {
                populateRewardForTransactions(transaction);
            }
        }
        return populateTransactionByMonth(customerTransactions, finalResponse);
    }

    private void populateRewardForTransactions(Transaction transaction) {
        if (null != transaction) {
            transaction.setReward(pointsByTransaction(transaction));
        }
    }

    private Set<String> getMonths(Set<Transaction> transactionSet) {
        return transactionSet.stream()
                .map(transaction -> transaction.getTransactionDateTime().getMonth().toString())
                .collect(Collectors.toSet());
    }

    private double pointsByTransaction(Transaction transaction) {
        if (transaction.getTransactionCost() <= 50) {
            return 0;
        } else if (transaction.getTransactionCost() <= 100) {
            return transaction.getTransactionCost() - 50;
        } else {
            return ((transaction.getTransactionCost() - 100) * 2) + 50;
        }
    }

    private Map<String, CustomerTransactionsRewardByMonth> populateTransactionByMonth(Map<String,
            CustomerTransactions> customerTransactions, Map<String, CustomerTransactionsRewardByMonth> finalResponse) {
        List<CustomerTransactions> customerTransactionsList = customerTransactions.entrySet().stream()
                .map(entry -> entry.getValue()).collect(Collectors.toList());

        for (CustomerTransactions customerTransaction : customerTransactionsList) {
            buildCustomerRewardByMonth(customerTransaction, finalResponse);

        }
        populateRewards(finalResponse);
        return finalResponse;
    }

    private void buildCustomerRewardByMonth(CustomerTransactions customerTransaction,
                                            Map<String, CustomerTransactionsRewardByMonth> finalResponse) {
        Set<String> months = getMonths(customerTransaction.getTransactionSet());
        Map<String, Set<Transaction>> transactionMap = new HashMap<>();
        for (String month : months) {
            CustomerTransactions customerTransactions = new CustomerTransactions();
            List<CustomerTransactions> customerTransactionsList = new ArrayList<>();
            CustomerTransactionsRewardByMonth customerTransactionsRewardByMonth = new CustomerTransactionsRewardByMonth();
            customerTransactionsRewardByMonth.setMonth(month);
            Set<Transaction> monthlyTransactions = new HashSet<>();
            monthlyTransactions.addAll(customerTransaction.getTransactionSet().stream().filter(transaction ->
                    transaction.getTransactionDateTime().getMonth().toString().equalsIgnoreCase(month)
            ).collect(Collectors.toList()));
            transactionMap.put(month, monthlyTransactions);
            customerTransactions.setCustomer(customerTransaction.getCustomer());
            if (!finalResponse.containsKey(month)) {
                populateNormalResponse(customerTransactionsList, customerTransactions, finalResponse,
                        customerTransactionsRewardByMonth, monthlyTransactions, month);
            } else {
                customerTransactionsList = finalResponse.get(month).getCustomerTransactions();
                populateNormalResponse(customerTransactionsList, customerTransactions, finalResponse,
                        customerTransactionsRewardByMonth, monthlyTransactions, month);
            }
            finalResponse.put(month, customerTransactionsRewardByMonth);
        }
    }

    private void populateRewards(Map<String, CustomerTransactionsRewardByMonth> finalResponse) {
        for (CustomerTransactionsRewardByMonth customerTransactionsRewardByMonth : finalResponse.values()) {
            List<CustomerTransactions> customerTransactions = customerTransactionsRewardByMonth.getCustomerTransactions();
            populateCustomerMonthlyTotal(customerTransactions);
            populateOverallMonthlyTotal(customerTransactionsRewardByMonth);
        }
    }

    private void populateCustomerMonthlyTotal(List<CustomerTransactions> customerTransactionList) {
        for (CustomerTransactions customerTrans : customerTransactionList) {
            double customerMonthTotal = 0.0;
            for (Transaction transaction : customerTrans.getTransactionSet()) {
                customerMonthTotal += transaction.getReward();
            }
            customerTrans.setCustomerRewardsTotalPerMonth(customerMonthTotal);
        }
    }

    private void populateOverallMonthlyTotal(CustomerTransactionsRewardByMonth customerTransactionsRewardByMonth) {
        double finalMonthTotal = 0.0;
        for (CustomerTransactions customerTransaction : customerTransactionsRewardByMonth.getCustomerTransactions()) {
            finalMonthTotal += customerTransaction.getCustomerRewardsTotalPerMonth();
        }
        customerTransactionsRewardByMonth.setTotalRewardPerMonth(finalMonthTotal);
    }

    private void populateNormalResponse(List<CustomerTransactions> customerTransactionsList,
                                        CustomerTransactions customerTransactions,
                                        Map<String, CustomerTransactionsRewardByMonth> finalResponse,
                                        CustomerTransactionsRewardByMonth customerTransactionsRewardByMonth,
                                        Set<Transaction> monthlyTransactions,
                                        String month) {
        if (!customerExists(customerTransactions, finalResponse, month)) {
            customerTransactions.setTransactionSet(monthlyTransactions);
            customerTransactionsList.add(customerTransactions);
            customerTransactionsRewardByMonth.setCustomerTransactions(customerTransactionsList);
        } else {
            customerTransactionsRewardByMonth = finalResponse.get(month);
            CustomerTransactions customerTransactions1 = customerTransactionsRewardByMonth.getCustomerTransactions()
                    .stream().filter(customerTrans -> customerTrans.getCustomer().getCustomerId()
                            .equalsIgnoreCase(customerTransactions.getCustomer().getCustomerId())).findFirst().get();
            customerTransactions1.getTransactionSet().addAll(monthlyTransactions);
        }
    }

    private boolean customerExists(CustomerTransactions customerTransactions,
                                   Map<String, CustomerTransactionsRewardByMonth> finalResponse, String month) {
        if (!finalResponse.containsKey(month)) {
            return false;
        }
        CustomerTransactionsRewardByMonth customerTransactionsRewardByMonth = finalResponse.get(month);
        return customerTransactionsRewardByMonth.getCustomerTransactions().stream()
                .map(CustomerTransactions::getCustomer)
                .anyMatch(customer -> customer.getCustomerId()
                        .equalsIgnoreCase(customerTransactions.getCustomer().getCustomerId()));
    }
}
