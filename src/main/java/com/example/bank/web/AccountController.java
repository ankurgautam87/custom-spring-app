package com.example.bank.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        return "accountForm";
    }

    @PostMapping
    public String processForm(@ModelAttribute("accountForm") AccountForm form, Model model) {
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
            return "accountDetails";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "accountForm";
        }
    }


}