package com.example.bank.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final BankAccountService bankAccountService;
    private final String successView;
    private final String formView;
    private final String commandName;
    private final Class<AccountForm> commandClass;


    public AccountController(BankAccountService bankAccountService, String successView, String formView, String commandName, Class<AccountForm> commandClass) {
        this.bankAccountService = bankAccountService;
        this.successView = successView;
        this.formView = formView;
        this.commandName = commandName;
        this.commandClass = commandClass;
    }

    @GetMapping
    public ModelAndView accountForm(Model model) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        model.addAttribute(commandName, commandClass.getDeclaredConstructor().newInstance());
        return new ModelAndView(formView);
    }

    @PostMapping
    public ModelAndView handleAccountAction(
            @ModelAttribute(commandName) AccountForm form, 
            BindingResult bindingResult, 
            Model model,
            HttpServletRequest request, 
            HttpServletResponse response) throws Exception {

        if (bindingResult.hasErrors()) {
            return new ModelAndView(formView);
        }

        String accountNumber = form.getAccountNumber();
        String action = form.getAction();

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
            model.addAttribute("error", e.getMessage());
            return new ModelAndView(formView); 
        }
        
        return new ModelAndView(successView);
    }
}