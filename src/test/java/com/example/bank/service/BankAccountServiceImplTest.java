package com.example.bank.service;

import com.example.bank.domain.BankAccount;
import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Map;

public class BankAccountServiceImplTest extends TestCase {

    private BankAccountService bankAccountService;
    private static final String VALID_ACCOUNT_NO = "12345";
    private static final String INVALID_ACCOUNT_NO = "99999";
    private static final double INITIAL_BALANCE = 1000.0;

    // This method is called before each test method execution.
    protected void setUp() throws Exception {
        super.setUp();

        // Create a new account object for our tests
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(INITIAL_BALANCE);

        // Create a map to hold the initial account data
        Map initialAccounts = new HashMap();
        initialAccounts.put(VALID_ACCOUNT_NO, account);
        
        // Initialize the service with the test data
        bankAccountService = new BankAccountServiceImpl(initialAccounts);
    }
    
    public void testGetAccountSuccess() {
        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertNotNull("Account should not be null", account);
        assertEquals("Account number should match", VALID_ACCOUNT_NO, account.getAccountNumber());
    }

    public void testGetAccountFailure() {
        try {
            bankAccountService.getAccount(INVALID_ACCOUNT_NO);
            fail("Should have thrown IllegalArgumentException for an invalid account number.");
        } catch (IllegalArgumentException e) {
            // Expected exception, so test passes.
            assertEquals("Exception message is incorrect", "Account not found", e.getMessage());
        }
    }
    
    public void testSuccessfulDeposit() {
        double depositAmount = 500.0;
        bankAccountService.deposit(VALID_ACCOUNT_NO, depositAmount);
        
        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertEquals("Balance should be updated after deposit", INITIAL_BALANCE + depositAmount, account.getBalance(), 0.0);
    }
    
    public void testDepositWithNegativeAmount() {
        try {
            bankAccountService.deposit(VALID_ACCOUNT_NO, -100.0);
            fail("Should have thrown IllegalArgumentException for negative deposit.");
        } catch (IllegalArgumentException e) {
            // Expected
            assertEquals("Deposit amount must be positive", e.getMessage());
        }
    }

    public void testSuccessfulWithdraw() {
        double withdrawAmount = 250.0;
        bankAccountService.withdraw(VALID_ACCOUNT_NO, withdrawAmount);
        
        BankAccount account = bankAccountService.getAccount(VALID_ACCOUNT_NO);
        assertEquals("Balance should be updated after withdrawal", INITIAL_BALANCE - withdrawAmount, account.getBalance(), 0.0);
    }

    public void testWithdrawWithInsufficientFunds() {
        try {
            bankAccountService.withdraw(VALID_ACCOUNT_NO, INITIAL_BALANCE + 1.0);
            fail("Should have thrown IllegalStateException for insufficient funds.");
        } catch (IllegalStateException e) {
            // Expected
            assertEquals("Insufficient funds", e.getMessage());
        }
    }
}
