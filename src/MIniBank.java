
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
                case OPEN_ACCOUNT ->
                        "Open Account — to be implemented in a later lab";
                case DEPOSIT ->
                        "Deposit — to be implemented in a later lab";
                case WITHDRAW ->
                        "Withdraw — to be implemented in a later lab";
                case TRANSFER ->
                        "Transfer — to be implemented in a later lab";
                case EXIT -> "Thank you for using MiniBank. Goodbye!";
            };

            System.out.println(message);

        } while (choice != 5);

        sc.close();
    }
}
