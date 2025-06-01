package com.example.bank.web;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        return "accountDetails";
    }

    @PostMapping
    public String submitForm(@Valid @ModelAttribute("accountForm") AccountForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "accountDetails";
        }

        try {
            String accountNumber = form.getAccountNumber();
            String action = form.getAction();

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
            model.addAttribute("error", e.getMessage());
            return "accountDetails";
        }

        return "accountDetails";
    }
}