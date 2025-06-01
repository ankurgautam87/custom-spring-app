package com.example.bank.domain;

// A simple POJO (Plain Old Java Object) to represent an account.
// Note the lack of modern features like annotations or Lombok.
public class BankAccount {
    private String accountNumber;
    private String ownerName;
    private double balance;

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
