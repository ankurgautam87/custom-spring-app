package com.example.bank.web;

// This object backs the HTML form. It holds the data submitted by the user.
public class AccountForm {
    private String accountNumber;
    private String action; // "deposit", "withdraw", "balance"
    private double amount;

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
}