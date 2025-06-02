package com.example.bank.service;

import com.example.bank.domain.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountServiceImplTest {

    private BankAccountService bankAccountService;
    private static final String VALID_ACCOUNT_NO = "12345";
    private static final String INVALID_ACCOUNT_NO = "99999";
    private static final double INITIAL_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        // Create a new account object for our tests
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(INITIAL_BALANCE);

        // Create a map to hold the initial account data
        Map<String, BankAccount> initialAccounts = new HashMap<>();
        initialAccounts.put(VALID_ACCOUNT_NO, account);

        // Initialize the service with the test data
        bankAccountService = new BankAccountServiceImpl(initialAccounts);
    }

    @Test
    void testGetAccountSuccess() {
        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertNotNull(account, "Account should not be null");
        assertEquals(VALID_ACCOUNT_NO, account.getAccountNumber(), "Account number should match");
    }

    @Test
    void testGetAccountFailure() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bankAccountService.getAccount(INVALID_ACCOUNT_NO));
        assertEquals("Account not found", exception.getMessage(), "Exception message is incorrect");
    }

    @Test
    void testSuccessfulDeposit() {
        double depositAmount = 500.0;
        bankAccountService.deposit(VALID_ACCOUNT_NO, depositAmount);

        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertEquals(INITIAL_BALANCE + depositAmount, account.getBalance(), 0.0, "Balance should be updated after deposit");
    }

    @Test
    void testDepositWithNegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bankAccountService.deposit(VALID_ACCOUNT_NO, -100.0));
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    @Test
    void testSuccessfulWithdraw() {
        double withdrawAmount = 250.0;
        bankAccountService.withdraw(VALID_ACCOUNT_NO, withdrawAmount);

        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertEquals(INITIAL_BALANCE - withdrawAmount, account.getBalance(), 0.0, "Balance should be updated after withdrawal");
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                bankAccountService.withdraw(VALID_ACCOUNT_NO, INITIAL_BALANCE + 1.0));
        assertEquals("Insufficient funds", exception.getMessage());
    }
}