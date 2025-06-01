package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.Map;

public class AccountControllerTest extends TestCase {

    private AccountController controller;
    private BankAccountService mockService;
    private static final String VALID_ACCOUNT_NO = "12345";
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // Setup a mock service for isolated controller testing
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(1000.0);
        
        Map initialAccounts = new HashMap();
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

    public void testSubmitDeposit() throws Exception {
        // Create the form backing object
        AccountForm form = new AccountForm();
        form.setAccountNumber(VALID_ACCOUNT_NO);
        form.setAction("deposit");
        form.setAmount(200.0);
        
        // Execute the onSubmit method
        ModelAndView mv = controller.onSubmit(form);
        
        // Assertions
        assertEquals("View name should be the success view", "accountResult", mv.getViewName());
        assertNotNull("Model should contain an account object", mv.getModel().get("account"));
        assertNotNull("Model should contain a success message", mv.getModel().get("message"));
        
        BankAccount updatedAccount = (BankAccount) mv.getModel().get("account");
        assertEquals("Balance should be updated", 1200.0, updatedAccount.getBalance(), 0.0);
    }
    
    public void testSubmitWithdraw() throws Exception {
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
    
    public void testSubmitBalanceCheck() throws Exception {
        AccountForm form = new AccountForm();
        form.setAccountNumber(VALID_ACCOUNT_NO);
        form.setAction("balance");
        
        ModelAndView mv = controller.onSubmit(form);
        
        assertEquals("accountResult", mv.getViewName());
        assertNotNull(mv.getModel().get("account"));
        assertNull("Message should be null for balance check", mv.getModel().get("message"));

        BankAccount account = (BankAccount) mv.getModel().get("account");
        assertEquals(1000.0, account.getBalance(), 0.0);
    }
    
    public void testSubmitWithInvalidAccount() throws Exception {
        AccountForm form = new AccountForm();
        form.setAccountNumber("INVALID_ID");
        form.setAction("deposit");
        form.setAmount(100);
        
        ModelAndView mv = controller.onSubmit(form);
        
        assertEquals("View name should be the form view on error", "accountForm", mv.getViewName());
        assertNotNull("Model should contain an error message", mv.getModel().get("error"));
        assertEquals("Account not found", mv.getModel().get("error"));
    }
}