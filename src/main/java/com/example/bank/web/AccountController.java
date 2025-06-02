package com.example.bank.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ModelAndView accountForm(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        return new ModelAndView("accountForm"); // Assumes "accountForm" is the view name
    }

    @PostMapping
    public ModelAndView handleAccountAction(@ModelAttribute("accountForm") AccountForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("accountForm"); // Return to form with errors
        }

        String accountNumber = form.getAccountNumber();
        String action = form.getAction();
        ModelAndView mv = new ModelAndView("account"); // Assumes "account" is the result view
        try {
            if ("deposit".equals(action)) {
                bankAccountService.deposit(accountNumber, form.getAmount());
                model.addAttribute("message", "Deposit successful.");
            } else if ("withdraw".equals(action)) {
                bankAccountService.withdraw(accountNumber, form.getAmount());
                model.addAttribute("message", "Withdrawal successful.");
            }
            
            BankAccount account = bankAccountService.getAccount(accountNumber);
            model.addAttribute("account", account);

        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., logging, specific error messages)
            model.addAttribute("error", e.getMessage());
            return new ModelAndView("accountForm");
        }
        
        return mv;
    }


}