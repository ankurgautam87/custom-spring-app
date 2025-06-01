package com.example.bank.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

    private BankAccountService bankAccountService;

    @Autowired
    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    @ResponseBody
    public AccountForm showForm() {
        return new AccountForm();
    }

    @PostMapping(produces = "application/json")
    public AccountDetails processForm(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam("action") String action,
            @RequestParam(value = "amount", required = false) Double amount
           ) {
        try {
            BankAccount account = bankAccountService.getAccount(accountNumber);
            String message = null;
            if ("deposit".equals(action)) {
                bankAccountService.deposit(accountNumber, amount);
                account.setBalance(account.getBalance() + amount);
                message = null;
            } else if ("withdraw".equals(action)) {
                bankAccountService.withdraw(accountNumber, amount);
                account.setBalance(account.getBalance() - amount);
                message = null;
            }

            return new AccountDetails(account, message);

        } catch (Exception e) {
           if (e instanceof IllegalArgumentException) {
               return new AccountDetails(null, "Invalid account number");
           } else {
               throw e;
           }
        }
    }

    public static class AccountForm {
        // Add form fields if necessary
    }
    public static class AccountDetails {
        private BankAccount account;
        private String message;
        // Constructor, getters and setters
        
        public AccountDetails(BankAccount account, String message) {
            this.account = account;
            this.message = message;
        }

        public BankAccount getAccount() {
            return account;
        }

        public void setAccount(BankAccount account) {
            this.account = account;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


}