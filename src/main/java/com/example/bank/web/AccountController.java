package com.example.bank.web;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ModelAndView handleAccountAction(@Valid @ModelAttribute("accountForm") AccountForm form) {
        ModelAndView mv = new ModelAndView("account"); // Set default view name

        try {
            String accountNumber = form.getAccountNumber();
            String action = form.getAction();
            
            if ("deposit".equals(action)) {
                bankAccountService.deposit(accountNumber, form.getAmount());
                mv.addObject("message", "Deposit successful.");
            } else if ("withdraw".equals(action)) {
                bankAccountService.withdraw(accountNumber, form.getAmount());
                mv.addObject("message", "Withdrawal successful.");
            }

            BankAccount account = bankAccountService.getAccount(accountNumber);
            mv.addObject("account", account);

        } catch (IllegalArgumentException | IllegalStateException e) {
            mv.setViewName("accountForm"); // Return to form view
            mv.addObject("error", e.getMessage());
            return mv; // Return immediately to avoid re-fetching account details
        }
        
        return mv;
    }


    @ModelAttribute("accountForm")
    public AccountForm getAccountForm() {
        return new AccountForm(); // Provide a default instance of the command object
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ModelAndView handleExceptions(Exception ex) {
        ModelAndView mv = new ModelAndView("accountForm");
        mv.addObject("error", ex.getMessage());
        return mv;
    }
}