package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AccountControllerTest {

    private MockMvc mockMvc;
    private BankAccountService mockService;
    private static final String VALID_ACCOUNT_NO = "12345";

    @BeforeEach
    public void setUp() {
        mockService = Mockito.mock(BankAccountService.class);
        AccountController controller = new AccountController(mockService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(1000.0);

        Mockito.when(mockService.getAccount(VALID_ACCOUNT_NO)).thenReturn(account);
        Mockito.when(mockService.getAccount("INVALID_ID")).thenReturn(null);
    }

    @Test
    public void testSubmitDeposit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "deposit")
                        .param("amount", "200.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("account", (BankAccount ba) -> ba.getBalance() == 1200.0));
    }

    @Test
    public void testSubmitWithdraw() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "withdraw")
                        .param("amount", "300.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("account", (BankAccount ba) -> ba.getBalance() == 700.0));
    }

    @Test
    public void testSubmitBalanceCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "balance"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account", (BankAccount ba) -> ba.getBalance() == 1000.0));
    }

    @Test
    public void testSubmitWithInvalidAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", "INVALID_ID")
                        .param("action", "deposit")
                        .param("amount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountForm"))
                .andExpect(model().attributeExists("error"));
    }
}