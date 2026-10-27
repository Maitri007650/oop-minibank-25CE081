
package util;

import model.Account;

public class StatementFormatter {

    public static String buildStatement(Account account) {
        if (account == null) {
            return "Account not found.";
        }

        StringBuilder statement = new StringBuilder();

        statement.append("========== Account Statement ==========\n");
        statement.append(account);
        statement.append("\n=======================================");

        return statement.toString();
    }
}
