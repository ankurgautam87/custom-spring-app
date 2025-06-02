package com.example.bank.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

@Controller
@RequestMapping("/account") // Example mapping, adapt as needed
public class AccountController {

    private BankAccountService bankAccountService;

    @Autowired
    public void setBankAccountService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute("accountForm") AccountForm form, ModelMap model) {
        
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

            return "successView"; // Replace with your success view name

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Re-bind the form data using the same model attribute name as in the form
            model.addAttribute("accountForm", form); 
            return "formView"; // Replace with your form view name
        }
    }


}