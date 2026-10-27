
package util;

import model.Command;
import model.TransactionType;

public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Use: DEPOSIT AC0001 500"
            );
        }

        try {
            TransactionType type =
                    TransactionType.valueOf(parts[0].toUpperCase());

            String accountNumber = parts[1];
            long amount = Long.parseLong(parts[2]);

            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "Amount must be positive"
                );
            }

            return new Command(type, accountNumber, amount);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid command. Example: DEPOSIT AC0001 500"
            );
        }
    }
}
