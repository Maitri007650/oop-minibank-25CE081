
public class PasswordChecker {

    public static String strength(String pw) {
        int score = 0;

        if (pw.length() >= 8) {
            score++;
        }
        if (pw.matches(".*[A-Z].*")) {
            score++;
        }
        if (pw.matches(".*[0-9].*")) {
            score++;
        }
        if (pw.matches(".*[^a-zA-Z0-9].*")) {
            score++;
        }

        if (score <= 1) {
            return "Weak";
        } else if (score <= 3) {
            return "Medium";
        } else {
            return "Strong";
        }
    }
}
