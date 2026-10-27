
package util;

import model.Account;
import model.Command;

public class Practical14Demo {

    public static void main(String[] args) {

        System.out.println("Practical 14 - Validation and Command Parsing");

        System.out.println("Valid mobile: " +
                Validator.isValidMobile("9876543210"));
        System.out.println("Invalid mobile: " +
                Validator.isValidMobile("12345"));

        System.out.println("Valid email: " +
                Validator.isValidEmail("riya@example.com"));
        System.out.println("Invalid email: " +
                Validator.isValidEmail("riya@"));

        System.out.println("Valid PAN: " +
                Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN: " +
                Validator.isValidPan("ABC123"));

        System.out.println("Valid IFSC: " +
                Validator.isValidIfsc("HDFC0001234"));
        System.out.println("Invalid IFSC: " +
                Validator.isValidIfsc("HDF123"));

        Command command =
                CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("Command type: " + command.type());
        System.out.println("Account number: " + command.accountNumber());
        System.out.println("Amount: " + command.amount());
    }
}
