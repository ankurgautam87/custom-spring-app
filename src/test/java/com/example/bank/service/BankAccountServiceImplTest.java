package com.example.bank.service;

import com.example.bank.domain.BankAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class BankAccountServiceImplTest {

    private BankAccountService bankAccountService;
    private static final String VALID_ACCOUNT_NO = "12345";
    private static final String INVALID_ACCOUNT_NO = "99999";
    private static final double INITIAL_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(INITIAL_BALANCE);

        Map<String, BankAccount> initialAccounts = new HashMap<>();
        initialAccounts.put(VALID_ACCOUNT_NO, account);

        bankAccountService = new BankAccountServiceImpl(initialAccounts);
    }

    @Test
    void getAccountSuccess() {
        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        Assertions.assertNotNull(account, "Account should not be null");
        Assertions.assertEquals(VALID_ACCOUNT_NO, account.getAccountNumber(), "Account number should match");
    }

    @Test
    void getAccountFailure() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> bankAccountService.getAccount(INVALID_ACCOUNT_NO), "Should have thrown IllegalArgumentException for an invalid account number.");

        try {
            bankAccountService.getAccount(INVALID_ACCOUNT_NO);
            Assertions.fail("Should have thrown IllegalArgumentException for an invalid account number.");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Account not found", e.getMessage(), "Exception message is incorrect");
        }

    }

    @Test
    void successfulDeposit() {
        double depositAmount = 500.0;
        bankAccountService.deposit(VALID_ACCOUNT_NO, depositAmount);

        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        Assertions.assertEquals(INITIAL_BALANCE + depositAmount, account.getBalance(), 0.0, "Balance should be updated after deposit");
    }

    @Test
    void depositWithNegativeAmount() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> bankAccountService.deposit(VALID_ACCOUNT_NO, -100.0), "Should have thrown IllegalArgumentException for negative deposit.");
    }

    @Test
    void successfulWithdraw() {
        double withdrawAmount = 250.0;
        bankAccountService.withdraw(VALID_ACCOUNT_NO, withdrawAmount);

        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        Assertions.assertEquals(INITIAL_BALANCE - withdrawAmount, account.getBalance(), 0.0, "Balance should be updated after withdrawal");
    }

    @Test
    void withdrawWithInsufficientFunds() {
       Assertions.assertThrows(IllegalStateException.class, () -> bankAccountService.withdraw(VALID_ACCOUNT_NO, INITIAL_BALANCE + 1.0), "Should have thrown IllegalStateException for insufficient funds.");
    }
}