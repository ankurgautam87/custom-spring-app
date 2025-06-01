package com.example.bank.service;

import com.example.bank.domain.BankAccount;

// Defines the contract for our business logic.
public interface BankAccountService {
    BankAccount getAccount(String accountNumber);
    void deposit(String accountNumber, double amount);
    void withdraw(String accountNumber, double amount);
}
