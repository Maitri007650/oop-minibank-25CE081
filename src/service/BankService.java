
package service;

import model.Account;
import model.SavingsAccount;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;

import java.util.ArrayList;
import java.util.List;

public class BankService {

    private final List<Account> accounts;
    private final Repository<Account> repository = new Repository<>();

    public BankService(List<Account> accounts) {
        this.accounts = accounts;

        for (Account account : accounts) {
            repository.save(account);
        }
    }

    public Account openAccount(String ownerName, long openingBalance)
            throws InvalidAmountException {

        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        Account account = new SavingsAccount(ownerName, openingBalance, 0);
        accounts.add(account);
        repository.save(account);

        return account;
    }

    public void deposit(String accountNumber, long amount)
            throws BankException, InvalidAmountException {

        getAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, long amount)
            throws BankException, InvalidAmountException,
            InsufficientFundsException {

        getAccount(accountNumber).withdraw(amount);
    }

    public void transfer(String fromNumber, String toNumber, long amount)
            throws BankException, InvalidAmountException,
            InsufficientFundsException {

        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);

        from.transfer(to, amount);
    }

    public Account statement(String accountNumber)
            throws AccountNotFoundException {

        return getAccount(accountNumber);
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(accounts);
    }

    public Account getAccount(String accountNumber)
            throws AccountNotFoundException {

        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                repository.save(account);
                return account;
            }
        }

        throw new AccountNotFoundException(
                "Account not found: " + accountNumber);
    }
    public void addExistingAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            repository.save(account);
        }
    }

    public Account openAccount(int type, String ownerName, long openingBalance) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        Account account;

        switch (type) {
            case 1 -> account = new SavingsAccount(ownerName, openingBalance, 1000);
            case 2 -> account = new model.CurrentAccount(ownerName, openingBalance, 2000);
            case 3 -> account = new model.FixedDepositAccount(ownerName, openingBalance);
            default -> throw new IllegalArgumentException("Invalid account type.");
        }

        accounts.add(account);
        repository.save(account);
        return account;
    }
}
