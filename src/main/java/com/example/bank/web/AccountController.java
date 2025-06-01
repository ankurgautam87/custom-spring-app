package com.example.bank.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import com.example.bank.domain.BankAccount;
import com.example.bank.service.BankAccountService;

// This controller handles all logic for the account page.
public class AccountController extends SimpleFormController {

    private BankAccountService bankAccountService;

    // Setter for Spring to inject the service bean
    public void setBankAccountService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    // This method is called when the form is submitted (POST request).
    protected ModelAndView onSubmit(Object command) throws Exception {
        AccountForm form = (AccountForm) command;
        ModelAndView mv = new ModelAndView(getSuccessView());
        
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
            
            // For all actions, we fetch and display the latest account state.
            BankAccount account = bankAccountService.getAccount(accountNumber);
            mv.addObject("account", account);

        } catch (Exception e) {
            // If anything goes wrong, we return to the form view with an error message.
            // A more robust app would differentiate between user errors and system errors.
            return new ModelAndView(getFormView())
                .addObject("error", e.getMessage())
                .addObject(getCommandName(), command); // Re-bind the form data
        }
        
        return mv;
    }
}