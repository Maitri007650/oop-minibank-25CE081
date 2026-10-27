
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
                new Account("Riya", 5000),
                new Account("Amit", 3000),
                new Account("Neha")
        };

        accounts[0].deposit(2000);
        accounts[0].withdraw(3000);
        boolean result = accounts[0].withdraw(10000);

        System.out.println("\nPractical 2 - Account Details");
        for (Account account : accounts) {
            System.out.println("Account: " + account.getAccountNumber());
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Balance: " + account.getBalance());
            System.out.println();
        }

        System.out.println("Withdrawal of 10000 successful: " + result);


        System.out.println("\nPractical 3 - Object Methods");

        for (Account account : accounts) {
            System.out.println(account);
        }

        Account anotherAccount = new Account(
                accounts[0].getAccountNumber(), "Riya", 4000);
        System.out.println("Two accounts equal: "
                + accounts[0].equals(anotherAccount));

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

        int choice;
        do {
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.println("Invalid input. Enter a number from 1 to 5.");
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
}
