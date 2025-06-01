package com.example.bank.service;

import com.example.bank.domain.BankAccount;
import java.util.Map;

// Implementation of the service. In Java 1.4, we can't use generics,
// so Maps and Lists are raw types.
public class BankAccountServiceImpl implements BankAccountService {

    // This map acts as our in-memory database.
    private Map accounts; // Raw type, no <String, BankAccount>

    public BankAccountServiceImpl(Map initialAccounts) {
        this.accounts = initialAccounts;
    }

    public BankAccount getAccount(String accountNumber) {
        BankAccount account = (BankAccount) accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }

    public void deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        BankAccount account = getAccount(accountNumber);
        account.setBalance(account.getBalance() + amount);
        accounts.put(accountNumber, account);
    }

    public void withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        BankAccount account = getAccount(accountNumber);
        if (account.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(account.getBalance() - amount);
        accounts.put(accountNumber, account);
    }
}