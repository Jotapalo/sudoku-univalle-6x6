package com.sudoku.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalLong;

/**
 * Persists simple game statistics to a plain text file.
 * <p>
 * The file stores one summary line per completed game and a small aggregate section so the
 * data is human-readable and easy to inspect without extra tools.
 * </p>
 */
public class StatisticsManager {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path STATS_FILE = Paths.get(
            System.getProperty("user.home"),
            ".sudoku36",
            "statistics.txt"
    );

    private StatisticsManager() {
    }

    /**
     * Appends a completed-game entry and refreshes the aggregate summary.
     *
     * @param result final result data for the completed game
     */
    public static synchronized void recordCompletedGame(GameResult result) {
        try {
            Files.createDirectories(STATS_FILE.getParent());

            StatisticsSnapshot snapshot = readSnapshot();
            snapshot.gamesPlayed++;
            snapshot.gamesWon++;
            snapshot.totalHintsUsed += result.hintsUsed();
            snapshot.totalTimeMs += result.elapsedMs();
            if (snapshot.bestTimeMs == 0 || result.elapsedMs() < snapshot.bestTimeMs) {
                snapshot.bestTimeMs = result.elapsedMs();
            }

            StringBuilder content = new StringBuilder();
            content.append("# Sudoku36 statistics\n");
            content.append("Updated: ").append(LocalDateTime.now().format(DATE_FORMAT)).append('\n');
            content.append("Games played: ").append(snapshot.gamesPlayed).append('\n');
            content.append("Games won: ").append(snapshot.gamesWon).append('\n');
            content.append("Total hints used: ").append(snapshot.totalHintsUsed).append('\n');
            content.append("Total time (ms): ").append(snapshot.totalTimeMs).append('\n');
            content.append("Best time (ms): ").append(snapshot.bestTimeMs).append('\n');
            content.append('\n');
            content.append("Recent result: ").append(result.formatAsLine()).append('\n');
            content.append('\n');
            content.append(snapshot.history);

            Files.writeString(STATS_FILE, content.toString(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Persistence failure must not break the game flow.
        }
    }

    /**
     * @return path of the statistics file used by the app
     */
    public static Path getStatsFile() {
        return STATS_FILE;
    }

    /**
     * Reads the current best time from the statistics file if it exists.
     *
     * @return best time in milliseconds, or empty if no statistics file is available yet
     */
    public static OptionalLong readBestTimeMs() {
        try {
            if (!Files.exists(STATS_FILE)) {
                return OptionalLong.empty();
            }

            List<String> lines = Files.readAllLines(STATS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("Best time (ms): ")) {
                    long value = parseLong(line);
                    return value > 0 ? OptionalLong.of(value) : OptionalLong.empty();
                }
            }
        } catch (IOException ignored) {
            // If stats cannot be read, the menu simply won't show the record.
        }
        return OptionalLong.empty();
    }

    private static StatisticsSnapshot readSnapshot() throws IOException {
        StatisticsSnapshot snapshot = new StatisticsSnapshot();
        if (!Files.exists(STATS_FILE)) {
            return snapshot;
        }

        List<String> lines = Files.readAllLines(STATS_FILE, StandardCharsets.UTF_8);
        StringBuilder history = new StringBuilder();
        boolean inHistory = false;
        for (String line : lines) {
            if (line.startsWith("Games played: ")) {
                snapshot.gamesPlayed = parseLong(line);
            } else if (line.startsWith("Games won: ")) {
                snapshot.gamesWon = parseLong(line);
            } else if (line.startsWith("Total hints used: ")) {
                snapshot.totalHintsUsed = parseLong(line);
            } else if (line.startsWith("Total time (ms): ")) {
                snapshot.totalTimeMs = parseLong(line);
            } else if (line.startsWith("Best time (ms): ")) {
                snapshot.bestTimeMs = parseLong(line);
            } else if (line.startsWith("Recent result: ")) {
                inHistory = true;
                history.append(line.substring("Recent result: ".length())).append('\n');
            } else if (inHistory) {
                history.append(line).append('\n');
            }
        }
        snapshot.history = history.toString();
        return snapshot;
    }

    private static long parseLong(String line) {
        int colon = line.indexOf(':');
        if (colon < 0) {
            return 0L;
        }
        String value = line.substring(colon + 1).trim();
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private static final class StatisticsSnapshot {
        private long gamesPlayed;
        private long gamesWon;
        private long totalHintsUsed;
        private long totalTimeMs;
        private long bestTimeMs;
        private String history = "";
    }

    /**
     * One finished game entry stored in the statistics file.
     *
     * @param finishedAt timestamp of completion
     * @param elapsedMs  final elapsed time in milliseconds
     * @param hintsUsed  hints consumed during the game
     */
    public record GameResult(LocalDateTime finishedAt, long elapsedMs, int hintsUsed) {

        /**
         * Formats the result as one human-readable line for the text file.
         *
         * @return plain-text summary
         */
        public String formatAsLine() {
            return finishedAt.format(DATE_FORMAT)
                    + " | timeMs=" + elapsedMs
                    + " | hintsUsed=" + hintsUsed;
        }
    }
}
