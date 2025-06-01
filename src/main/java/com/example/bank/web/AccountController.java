package com.example.bank.web;

import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/account") // Example mapping, adjust as needed
public class AccountController {

    private final BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ModelAndView> submit(@ModelAttribute AccountForm form, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("success"); // Replace with your success view name

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

            return ResponseEntity.ok(mv);

        } catch (Exception e) {
            mv = new ModelAndView("form"); // Replace with your form view name
            mv.addObject("error", e.getMessage());
            mv.addObject("accountForm", form); // Re-bind the form data using the command name "accountForm"
            return ResponseEntity.badRequest().body(mv); // Return a 400 Bad Request with the error
        }
    }
}