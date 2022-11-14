package com.assignment.charter.dao;

import com.assignment.charter.domain.Customer;
import com.assignment.charter.domain.CustomerTransactions;
import com.assignment.charter.domain.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class CustomerTransactionRepository {
    @Autowired
    ResourceLoader resourceLoader;
    public Map<String, CustomerTransactions> getCustomerTransactions() {
        Map<String, CustomerTransactions> customerTransactionMap = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Resource resource = resourceLoader.getResource("classpath:customertransactions.json");
            JsonNode customerTransactions = mapper.readTree(resource.getFile());
            customerTransactions.get("customerTransactions").forEach(item->{
                Customer customer = new Customer();
                customer.setCustomerId(item.get("id").asText());
                customer.setCustomerFullName(item.get("customerFullName").asText());
                Set<Transaction> transactions = mapper.convertValue(item.get("transactions"), new TypeReference<Set<Transaction>>() {});
                CustomerTransactions customerTransaction = new CustomerTransactions();
                customerTransaction.setCustomer(customer);
                customerTransaction.setTransactionSet(transactions);
                customerTransactionMap.put(customer.getCustomerId(), customerTransaction);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customerTransactionMap;
    }
}
