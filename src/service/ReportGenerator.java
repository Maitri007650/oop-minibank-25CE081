
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        Path parent = reportFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        long totalLines = 0;

        StringBuilder report = new StringBuilder();

        report.append("MiniBank Transaction Report\n");
        report.append("===========================\n");

        if (Files.exists(logsFolder) && Files.isDirectory(logsFolder)) {
            try (DirectoryStream<Path> files =
                         Files.newDirectoryStream(logsFolder)) {

                for (Path file : files) {
                    if (!Files.isRegularFile(file)) {
                        continue;
                    }

                    BasicFileAttributes attributes =
                            Files.readAttributes(
                                    file, BasicFileAttributes.class);

                    report.append("\nFile: ")
                            .append(file.getFileName())
                            .append("\nSize: ")
                            .append(attributes.size())
                            .append(" bytes\n");

                    try (BufferedReader reader = Files.newBufferedReader(
                            file, StandardCharsets.UTF_8)) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            totalLines++;

                            String lowerLine = line.toLowerCase();

                            if (lowerLine.contains("deposit")) {
                                totalDeposits += extractAmount(line);
                            }

                            if (lowerLine.contains("withdraw")
                                    || lowerLine.contains("withdrew")) {
                                totalWithdrawals += extractAmount(line);
                            }

                            report.append(line).append("\n");
                        }
                    }
                }
            }
        } else {
            report.append("\nLogs folder does not exist yet.\n");
        }

        report.append("\nSummary\n");
        report.append("-------\n");
        report.append("Total log lines: ").append(totalLines).append("\n");
        report.append("Total deposits: ").append(totalDeposits).append("\n");
        report.append("Total withdrawals: ").append(totalWithdrawals).append("\n");

        Files.writeString(
                reportFile,
                report.toString(),
                StandardCharsets.UTF_8
        );
    }

    private static long extractAmount(String line) {
        String[] parts = line.split("\\D+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                try {
                    return Long.parseLong(part);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }

        return 0;
    }
}
