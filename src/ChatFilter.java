
import java.util.Scanner;

public class ChatFilter {
    public static void main(String[] args) {

        String[] logs = {
                "10:05 alice Hello there",
                "10:10 bob How are you",
                "10:15 riya Hello everyone",
                "malformed line"
        };

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine();

        StringBuilder report = new StringBuilder();
        int count = 0;

        for (String log : logs) {
            String[] parts = log.split(" ", 3);

            if (parts.length < 3) {
                continue;
            }

            String message = parts[2];

            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                count++;
                report.append(parts[0])
                        .append(" ")
                        .append(parts[1])
                        .append(": ")
                        .append(message)
                        .append("\n");
            }
        }

        System.out.println("Matches: " + count);
        System.out.println(report);
        sc.close();
    }
}
