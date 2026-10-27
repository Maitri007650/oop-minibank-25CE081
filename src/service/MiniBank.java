
package service;

import util.AnnotationValidator;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

record BankInfo(String name, String branch) {
}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo("MiniBank", "Main Branch");
        System.out.println("===== " + bank.name() + " =====");
        System.out.println("Branch: " + bank.branch());

        Account[] accounts = {
                new SavingsAccount("Riya", 5000, 1000),
                new CurrentAccount("Amit", 3000, 2000),
                new FixedDepositAccount("Neha", 10000)
        };

        // Practical 8 - Exception Handling
        System.out.println("\nPractical 8 - Exception Handling");

        try {
            accounts[0].deposit(2000);
            accounts[0].withdraw(3000);
            System.out.println("Deposit and withdrawal successful.");
            logTransaction("Riya deposited 2000");
            logTransaction("Riya withdrew 3000");
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: short by "
                    + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            System.out.println("Transaction attempt completed.");
        }

        try {
            accounts[0].withdraw(10000);
            System.out.println("Withdrawal successful.");
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: short by "
                    + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }

        // Practical 2 - Account Details
        System.out.println("\nPractical 2 - Account Details");

        for (Account account : accounts) {
            System.out.println("Account: " + account.getAccountNumber());
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Balance: " + account.getBalance());
            System.out.println();
        }

        // Practical 3 - Object Methods
        System.out.println("\nPractical 3 - Object Methods");

        for (Account account : accounts) {
            System.out.println(account);
        }

        Account anotherAccount = accounts[0];

        System.out.println("Two accounts equal: "
                + accounts[0].equals(anotherAccount));

        // Practical 5 - Inheritance and Polymorphism
        System.out.println("\nPractical 5 - Inheritance and Polymorphism");

        for (Account account : accounts) {
            System.out.println(account.getOwnerName()
                    + " interest rate: " + account.interestRate());

            if (account instanceof SavingsAccount) {
                System.out.println("This is a Savings Account");
            }
        }

        Object obj = accounts[0];

        System.out.println("Object is an Account: "
                + (obj instanceof Account));

        Customer customer = new Customer(
                "Riya", "riya@example.com", "9876543210");

        Customer.Address address = new Customer.Address(
                "12 Main Road", "Ahmedabad", "380001");

        customer.setAddress(address);

        Customer copiedCustomer = customer.clone();

        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Address city: "
                + customer.getAddress().getCity());
        System.out.println("Cloned customer: "
                + copiedCustomer.getName());

        // Practical 6 - Interfaces and Lambda
        System.out.println("\nPractical 6 - Interfaces and Lambda");

        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        WithdrawRule rule2 =
                (account, amount) -> account.canWithdraw(amount);

        System.out.println("Anonymous rule: "
                + rule1.allow(accounts[0], 1000));
        System.out.println("Lambda rule: "
                + rule2.allow(accounts[0], 1000));
        System.out.println("Yearly interest: "
                + accounts[0].yearlyInterest());

        // Practical 7 - Annotations and Reflection
        System.out.println("\nPractical 7 - Annotations and Reflection");

        SavingsAccount invalidAccount =
                new SavingsAccount("Test", 100, 1000);

        try {
            java.lang.reflect.Field balanceField =
                    Account.class.getDeclaredField("balance");

            balanceField.setAccessible(true);
            balanceField.setLong(invalidAccount, -100);

            String[] errors = AnnotationValidator.validate(invalidAccount);

            for (String error : errors) {
                System.out.println("Validation error: " + error);
            }
        } catch (ReflectiveOperationException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }

        // Practical 8 - Try-with-resources
        System.out.println("\nTry-with-resources demonstration:");

        try (MiniBankResource resource = new MiniBankResource()) {
            System.out.println("Using bank resource.");
            throw new IllegalStateException("Demo exception");
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Practical 11 - Persistence and Reporting
        System.out.println("\nPractical 11 - Persistence and Reporting");

        Path dataFile = Path.of("data", "accounts.dat");
        Path logsFolder = Path.of("logs");
        Path reportFile = Path.of("reports", "bank-report.txt");

        try {
            StatePersister.save(accounts, dataFile);
            System.out.println("Accounts saved successfully.");

            Account[] loadedAccounts = StatePersister.load(dataFile);
            System.out.println("Accounts loaded successfully:");

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            ReportGenerator.generate(logsFolder, reportFile);
            System.out.println("Report generated at: " + reportFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Persistence/report error: "
                    + e.getMessage());
        }

        // Main Menu
        int choice;

        do {
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.println(
                        "Invalid input. Enter a number from 1 to 5.");
                sc.next();
                System.out.print("Enter your choice: ");
            }

            choice = sc.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                default -> MenuOption.EXIT;
            };

            String message = switch (option) {
                case OPEN_ACCOUNT -> "Open Account — later lab";
                case DEPOSIT -> "Deposit — later lab";
                case WITHDRAW -> "Withdraw — later lab";
                case TRANSFER -> "Transfer — later lab";
                case EXIT -> "Thank you for using MiniBank. Goodbye!";
            };

            System.out.println(message);

        } while (choice != 5);

        sc.close();
    }

    private static void logTransaction(String message) {
        try {
            TransactionLog.append(
                    Path.of("logs", "transactions.txt"), message);
        } catch (IOException e) {
            System.out.println("Could not write transaction log: "
                    + e.getMessage());
        }
    }

    static class MiniBankResource implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Resource closed.");
        }
    }
}
