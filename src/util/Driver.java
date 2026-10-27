package util;

public class Driver {
    public static void main(String[] args) {

        String[] passwords = {
                "abc",
                "Abcd1234!",
                "hello123",
                "HELLO"
        };

        for (String pw : passwords) {
            System.out.println("Password: " + pw);
            System.out.println("Strength: "
                    + PasswordChecker.strength(pw));
            System.out.println();
        }
    }
}
