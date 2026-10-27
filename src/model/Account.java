
package model;

import model.annotation.Id;
import model.annotation.Positive;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

import java.io.Serializable;
import java.util.Objects;

public abstract class Account
        implements Transactable, InterestBearing, Serializable,
        Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive
    protected long balance;

    private boolean active;
    private static int accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public static void updateCounterFromAccounts(Account[] accounts) {
        for (Account account : accounts) {
            if (account != null) {
                String number = account.getAccountNumber();

                try {
                    int value = Integer.parseInt(number.substring(2));
                    if (value > accountCounter) {
                        accountCounter = value;
                    }
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    // Ignore account numbers that don't match the expected format.
                }
            }
        }
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = Math.max(0, openingBalance);
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be positive");
        }

        if (!active) {
            throw new InvalidAmountException("Account is inactive");
        }

        balance += amount;
    }

    public void withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive");
        }

        if (!active) {
            throw new InvalidAmountException("Account is inactive");
        }

        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }

        if (!canWithdraw(amount)) {
            throw new InvalidAmountException(
                    "Withdrawal is not allowed");
        }

        balance -= amount;
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null) {
                throw new BankException("Destination account not found");
            }

            withdraw(amount);
            to.deposit(amount);

        } catch (BankException e) {
            throw e;
        } finally {
            System.out.println("Transfer operation completed.");
        }
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;

        Account other = (Account) obj;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
