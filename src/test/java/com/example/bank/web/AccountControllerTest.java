package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import com.example.bank.service.BankAccountServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountControllerTest.TestConfig.class)
@AutoConfigureMockMvc
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
        String jsonString = "{\"accountNumber\": \"" + VALID_ACCOUNT_NO + "\", \"action\": \"deposit\", \"amount\": 200.0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"));

        Mockito.verify(mockService).deposit(VALID_ACCOUNT_NO, 200.0);
    }

    @Test
    public void testSubmitWithdraw() throws Exception {
        String jsonString = "{\"accountNumber\": \"" + VALID_ACCOUNT_NO + "\", \"action\": \"withdraw\", \"amount\": 300.0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"));

        Mockito.verify(mockService).withdraw(VALID_ACCOUNT_NO, 300.0);
    }

    @Test
    public void testSubmitBalanceCheck() throws Exception {
        String jsonString = "{\"accountNumber\": \"" + VALID_ACCOUNT_NO + "\", \"action\": \"balance\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(view().name("accountResult"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeDoesNotExist("message"));

        Mockito.verify(mockService).getAccount(VALID_ACCOUNT_NO);
    }

    @Test
    public void testSubmitWithInvalidAccount() throws Exception {
        String jsonString = "{\"accountNumber\": \"INVALID_ID\", \"action\": \"deposit\", \"amount\": 100.0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(view().name("accountForm"))
                .andExpect(model().attributeExists("error"));

        Mockito.verify(mockService, Mockito.never()).deposit(Mockito.anyString(), Mockito.anyDouble());
    }

    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackages = "com.example.bank.web")
    static class TestConfig {

        @Bean
        public BankAccountServiceImpl bankAccountService() {
            Map<String, BankAccount> accountMap = new HashMap<>();
            return new BankAccountServiceImpl(accountMap);
        }
    }
}