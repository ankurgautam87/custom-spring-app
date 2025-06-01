package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService mockService;

    private static final String VALID_ACCOUNT_NO = "12345";

    @Before
    public void setUp() {
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
                .andExpect(model().attributeExists("message"));

        Mockito.verify(mockService).deposit(VALID_ACCOUNT_NO, 200.0);
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
                .andExpect(model().attributeExists("message"));

        Mockito.verify(mockService).withdraw(VALID_ACCOUNT_NO, 300.0);
    }

    @Test
    public void testSubmitBalanceCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .param("accountNumber", VALID_ACCOUNT_NO)
                        .param("action", "balance"))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeDoesNotExist("message"));

        Mockito.verify(mockService).getAccount(VALID_ACCOUNT_NO);
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

        Mockito.verify(mockService, Mockito.never()).deposit(Mockito.anyString(), Mockito.anyDouble());
    }
}