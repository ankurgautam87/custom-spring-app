package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService mockService;

    private static final String VALID_ACCOUNT_NO = "12345";

    @BeforeEach
    void setUp() {
        BankAccount account = new BankAccount();
        account.setAccountNumber(VALID_ACCOUNT_NO);
        account.setOwnerName("Test Owner");
        account.setBalance(1000.0);

        when(mockService.getAccount(VALID_ACCOUNT_NO)).thenReturn(Optional.of(account));
        when(mockService.getAccount("INVALID_ID")).thenReturn((Optional<BankAccount>)Optional.empty());
    }

    @Test
    public void testDeposit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "deposit")
                        .param("amount", "200.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account", hasProperty("balance", is(1200.0))));
    }

    @Test
    public void testWithdraw() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "withdraw")
                        .param("amount", "300.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account", hasProperty("balance", is(700.0))));
    }

    @Test
    public void testBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "balance"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account", hasProperty("balance", is(1000.0))));
    }

    @Test
    public void testInvalidAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", "INVALID_ID")
                        .param("action", "deposit")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountForm"))
                .andExpect(model().attribute("error", "Account not found"));
    }
}