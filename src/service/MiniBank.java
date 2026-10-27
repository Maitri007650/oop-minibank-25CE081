
package service;

import exception.BankException;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

record BankInfo(String name, String branch) {
}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER,
    VIEW_ACCOUNTS, VIEW_HISTORY, VIEW_STATEMENT,
    RUN_REPORT, SAVE_EXIT
}

public class MiniBank {

    private static final Scanner sc = new Scanner(System.in);

    private static final Path DATA_FILE =
            Path.of("data", "accounts.dat");

    private static final Path LOG_FILE =
            Path.of("logs", "transactions.txt");

    private static final Path REPORT_FILE =
            Path.of("reports", "bank-report.txt");

    private static final List<Account> accounts = new ArrayList<>();
    private static final BankService bankService =
            new BankService(accounts);

    public static void main(String[] args) {

        BankInfo bank = new BankInfo("MiniBank", "Main Branch");

        System.out.println("\n==================================");
        System.out.println("          " + bank.name());
        System.out.println("       Banking Application");
        System.out.println("==================================");
        System.out.println("Branch: " + bank.branch());

        loadAccounts();

        boolean running = true;

        while (running) {
            showMenu();

            int choice = readInt("Enter your choice: ");

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.VIEW_ACCOUNTS;
                case 6 -> MenuOption.VIEW_HISTORY;
                case 7 -> MenuOption.VIEW_STATEMENT;
                case 8 -> MenuOption.RUN_REPORT;
                case 9 -> MenuOption.SAVE_EXIT;
                default -> null;
            };

            if (option == null) {
                System.out.println("Invalid choice. Please select 1-9.");
                continue;
            }

            switch (option) {
                case OPEN_ACCOUNT -> openAccount();
                case DEPOSIT -> deposit();
                case WITHDRAW -> withdraw();
                case TRANSFER -> transfer();
                case VIEW_ACCOUNTS -> viewAccounts();
                case VIEW_HISTORY -> viewHistory();
                case VIEW_STATEMENT -> viewStatement();
                case RUN_REPORT -> generateReport();

                case SAVE_EXIT -> {
                    saveAccounts();
                    generateReport();
                    System.out.println("\nThank you for using MiniBank.");
                    System.out.println("Your session has ended.");
                    running = false;
                }
            }
        }

        sc.close();
    }

    private static void showMenu() {
        System.out.println("\n------------- MAIN MENU -------------");
        System.out.println("1. Open New Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. View All Accounts");
        System.out.println("6. View Transaction History");
        System.out.println("7. View Account Statement");
        System.out.println("8. Generate Bank Report");
        System.out.println("9. Save and Exit");
        System.out.println("-------------------------------------");
    }

    private static void openAccount() {
        System.out.println("\n--- Open New Account ---");

        System.out.print("Enter account holder name: ");
        String name = sc.nextLine().trim();

        if (name.isBlank()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.println("Account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");

        int type = readInt("Choose account type: ");

        if (type < 1 || type > 3) {
            System.out.println("Invalid account type.");
            return;
        }

        long openingBalance = readLong("Enter opening balance: ");

        if (openingBalance < 0) {
            System.out.println("Opening balance cannot be negative.");
            return;
        }

        try {
            Account account = bankService.openAccount(
                    type, name, openingBalance);

            saveAccounts();

            recordTransaction("Account opened: "
                    + account.getAccountNumber()
                    + " for " + name);

            System.out.println("\nAccount opened successfully!");
            System.out.println("Account number: "
                    + account.getAccountNumber());
            System.out.println("Account holder: "
                    + account.getOwnerName());
            System.out.println("Account type: "
                    + accountType(account));
            System.out.println("Opening balance: "
                    + account.getBalance());

        } catch (IllegalArgumentException e) {
            System.out.println("Account creation failed: "
                    + e.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("\n--- Deposit Money ---");

        Account account = selectAccount();

        if (account == null) {
            return;
        }

        long amount = readLong("Enter deposit amount: ");

        try {
            bankService.deposit(account.getAccountNumber(), amount);

            recordTransaction(account.getAccountNumber()
                    + " deposited " + amount);

            saveAccounts();

            System.out.println("Deposit successful!");
            System.out.println("Updated balance: "
                    + account.getBalance());

        } catch (BankException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void withdraw() {
        System.out.println("\n--- Withdraw Money ---");

        Account account = selectAccount();

        if (account == null) {
            return;
        }

        long amount = readLong("Enter withdrawal amount: ");

        try {
            bankService.withdraw(account.getAccountNumber(), amount);

            recordTransaction(account.getAccountNumber()
                    + " withdrew " + amount);

            saveAccounts();

            System.out.println("Withdrawal successful!");
            System.out.println("Remaining balance: "
                    + account.getBalance());

        } catch (BankException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void transfer() {
        System.out.println("\n--- Transfer Money ---");

        System.out.print("Enter sender account number: ");
        String fromNumber = sc.nextLine().trim();

        Account from = findAccount(fromNumber);

        if (from == null) {
            System.out.println("Sender account not found.");
            return;
        }

        System.out.print("Enter receiver account number: ");
        String toNumber = sc.nextLine().trim();

        Account to = findAccount(toNumber);

        if (to == null) {
            System.out.println("Receiver account not found.");
            return;
        }

        if (from == to) {
            System.out.println(
                    "Sender and receiver must be different.");
            return;
        }

        long amount = readLong("Enter transfer amount: ");

        try {
            bankService.transfer(fromNumber, toNumber, amount);

            recordTransaction(fromNumber + " transferred "
                    + amount + " to " + toNumber);

            saveAccounts();

            System.out.println("Transfer successful!");
            System.out.println("Sender balance: "
                    + from.getBalance());
            System.out.println("Receiver balance: "
                    + to.getBalance());

        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void viewAccounts() {
        System.out.println("\n--- Account List ---");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.printf("%-12s %-20s %-20s %s%n",
                "Account No.", "Holder", "Account Type", "Balance");

        System.out.println(
                "---------------------------------------------------------------");

        for (Account account : accounts) {
            System.out.printf("%-12s %-20s %-20s %d%n",
                    account.getAccountNumber(),
                    account.getOwnerName(),
                    accountType(account),
                    account.getBalance());
        }

        System.out.println("Total accounts: " + accounts.size());
    }

    private static void viewHistory() {
        System.out.println("\n--- Transaction History ---");

        if (!Files.exists(LOG_FILE)) {
            System.out.println("No transaction history available.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(LOG_FILE);

            if (lines.isEmpty()) {
                System.out.println("No transactions recorded.");
                return;
            }

            for (String line : lines) {
                System.out.println("- " + line);
            }

        } catch (IOException e) {
            System.out.println("Could not read history: "
                    + e.getMessage());
        }
    }

    private static void viewStatement() {
        System.out.println("\n--- Account Statement ---");

        System.out.print("Enter account number: ");
        String number = sc.nextLine().trim();

        try {
            Account account = bankService.statement(number);

            System.out.println("Account number: "
                    + account.getAccountNumber());
            System.out.println("Account holder: "
                    + account.getOwnerName());
            System.out.println("Account type: "
                    + accountType(account));
            System.out.println("Current balance: "
                    + account.getBalance());

        } catch (BankException e) {
            System.out.println("Could not display statement: "
                    + e.getMessage());
        }
    }

    private static Account selectAccount() {
        System.out.print("Enter account number: ");
        String number = sc.nextLine().trim();

        Account account = findAccount(number);

        if (account == null) {
            System.out.println(
                    "Account not found. Check the account number.");
        }

        return account;
    }

    private static Account findAccount(String number) {
        for (Account account : accounts) {
            if (account.getAccountNumber()
                    .equalsIgnoreCase(number)) {
                return account;
            }
        }

        return null;
    }

    private static String accountType(Account account) {
        if (account instanceof SavingsAccount) {
            return "Savings";
        }

        if (account instanceof CurrentAccount) {
            return "Current";
        }

        if (account instanceof FixedDepositAccount) {
            return "Fixed Deposit";
        }

        return "Other";
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole amount.");
            }
        }
    }

    private static void loadAccounts() {
        try {
            if (Files.exists(DATA_FILE)) {
                Account[] saved = StatePersister.load(DATA_FILE);

                accounts.clear();
                accounts.addAll(List.of(saved));

                Account.updateCounterFromAccounts(saved);

                System.out.println("Saved accounts loaded: "
                        + accounts.size());

            } else {
                accounts.add(new SavingsAccount("Riya", 5000, 1000));
                accounts.add(new CurrentAccount("Amit", 3000, 2000));
                accounts.add(new FixedDepositAccount("Neha", 10000));

                saveAccounts();

                System.out.println(
                        "Sample accounts created for testing.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load saved accounts: "
                    + e.getMessage());

            System.out.println("Starting with sample accounts.");

            accounts.clear();
            accounts.add(new SavingsAccount("Riya", 5000, 1000));
            accounts.add(new CurrentAccount("Amit", 3000, 2000));
            accounts.add(new FixedDepositAccount("Neha", 10000));
        }
    }

    private static void saveAccounts() {
        try {
            StatePersister.save(
                    accounts.toArray(new Account[0]), DATA_FILE);

        } catch (IOException e) {
            System.out.println("Could not save accounts: "
                    + e.getMessage());
        }
    }

    private static void recordTransaction(String message) {
        try {
            TransactionLog.append(LOG_FILE, message);

        } catch (IOException e) {
            System.out.println("Could not save transaction: "
                    + e.getMessage());
        }
    }

    private static void generateReport() {
        try {
            ReportGenerator.generate(
                    LOG_FILE.getParent(), REPORT_FILE);

            System.out.println("Report saved to " + REPORT_FILE);

        } catch (IOException e) {
            System.out.println("Could not generate report: "
                    + e.getMessage());
        }
    }
}
