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
public class AccountController {

    private final BankAccountService bankAccountService;
    private final String commandName;
    private final Class<AccountForm> commandClass;


    public AccountController(BankAccountService bankAccountService, String commandName, Class<AccountForm> commandClass) {
        this.bankAccountService = bankAccountService;
        this.commandName = commandName;
        this.commandClass = commandClass;
    }

    @GetMapping("/accountForm")
    public ModelAndView accountForm(Model model) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        model.addAttribute(commandName, commandClass.getDeclaredConstructor().newInstance());
        return new ModelAndView("accountForm");
    }

    @PostMapping("/account")
    public ModelAndView handleAccountAction(
            @ModelAttribute(commandName) AccountForm form, 
            BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("accountForm");
        }

        String accountNumber = form.getAccountNumber();
        String action = form.getAction();
        ModelAndView modelAndView = new ModelAndView("account");

        try {
            if ("deposit".equals(action)) {
                bankAccountService.deposit(accountNumber, form.getAmount());
                modelAndView.addObject("message", "Deposit successful.");
            } else if ("withdraw".equals(action)) {
                bankAccountService.withdraw(accountNumber, form.getAmount());
                modelAndView.addObject("message", "Withdrawal successful.");
            }
            
            BankAccount account = bankAccountService.getAccount(accountNumber);
            modelAndView.addObject("account", account);

        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("accountForm");
        }
        
        return modelAndView;
    }
}