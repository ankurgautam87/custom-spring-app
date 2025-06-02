package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {

    private AccountController controller;
    private BankAccountService mockService;
    private static final String VALID_ACCOUNT_NO = "12345";

    @BeforeEach
    void setUp() {
        // Setup a mock service for isolated controller testing
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(1000.0);

        Map<String, BankAccount> initialAccounts = new HashMap<>();
        initialAccounts.put(VALID_ACCOUNT_NO, account);

        mockService = new com.example.bank.service.BankAccountServiceImpl(initialAccounts);

        // Setup the controller
        controller = new AccountController();
        controller.setBankAccountService(mockService);
        controller.setSuccessView("accountResult"); // Set properties as they are in XML
        controller.setFormView("accountForm");
        controller.setCommandName("accountCommand");
        controller.setCommandClass(AccountForm.class);
    }

    @Test
    void submitDeposit() throws Exception {
        // Create the form backing object
        AccountForm form = new AccountForm();
        form.setAccountNumber(VALID_ACCOUNT_NO);
        form.setAction("deposit");
        form.setAmount(200.0);

        // Execute the onSubmit method
        ModelAndView mv = controller.onSubmit(form);

        // Assertions
        assertEquals("accountResult", mv.getViewName());
        assertNotNull(mv.getModel().get("account"));
        assertNotNull(mv.getModel().get("message"));

        BankAccount updatedAccount = (BankAccount) mv.getModel().get("account");
        assertEquals(1200.0, updatedAccount.getBalance(), 0.0);
    }

    @Test
    void submitWithdraw() throws Exception {
        AccountForm form = new AccountForm();
        form.setAccountNumber(VALID_ACCOUNT_NO);
        form.setAction("withdraw");
        form.setAmount(300.0);

        ModelAndView mv = controller.onSubmit(form);

        assertEquals("accountResult", mv.getViewName());
        assertNotNull(mv.getModel().get("account"));
        assertNotNull(mv.getModel().get("message"));

        BankAccount updatedAccount = (BankAccount) mv.getModel().get("account");
        assertEquals(700.0, updatedAccount.getBalance(), 0.0);
    }

    @Test
    void submitBalanceCheck() throws Exception {
        AccountForm form = new AccountForm();
        form.setAccountNumber(VALID_ACCOUNT_NO);
        form.setAction("balance");

        ModelAndView mv = controller.onSubmit(form);

        assertEquals("accountResult", mv.getViewName());
        assertNotNull(mv.getModel().get("account"));
        assertNull(mv.getModel().get("message"));

        BankAccount account = (BankAccount) mv.getModel().get("account");
        assertEquals(1000.0, account.getBalance(), 0.0);
    }

    @Test
    void submitWithInvalidAccount() throws Exception {
        AccountForm form = new AccountForm();
        form.setAccountNumber("INVALID_ID");
        form.setAction("deposit");
        form.setAmount(100);

        ModelAndView mv = controller.onSubmit(form);

        assertEquals("accountForm", mv.getViewName());
        assertNotNull(mv.getModel().get("error"));
        assertEquals("Account not found", mv.getModel().get("error"));
    }
}