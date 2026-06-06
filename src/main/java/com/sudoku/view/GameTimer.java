package com.sudoku.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Elapsed-time stopwatch for an active Sudoku round, driven by a JavaFX {@link Timeline}.
 * <p>
 * Fires a callback once per second with a {@code mm:ss} string while running. When stopped,
 * elapsed time is frozen until {@link #reset()} or {@link #start()}.
 * </p>
 */
public class GameTimer {

    /** Receives formatted time on each tick and after {@link #reset()}. */
    private final Consumer<String> onTick;

    private Timeline timeline;
    private long startNanos;
    private long elapsedMs;
    private boolean running;

    /**
     * @param onTick callback invoked with {@link #formatElapsed()}; may be {@code null} to skip updates
     */
    public GameTimer(Consumer<String> onTick) {
        this.onTick = onTick;
    }

    /**
     * Resets elapsed time to zero, starts counting, and begins one-second UI updates.
     */
    public void start() {
        reset();
        running = true;
        startNanos = System.nanoTime();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> notifyTick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        notifyTick();
    }

    /**
     * Stops the timeline and records elapsed time so {@link #getElapsedMs()} stays constant.
     */
    public void stop() {
        if (running) {
            elapsedMs = getElapsedMs();
            running = false;
        }
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Stops the timer and clears elapsed time to zero.
     */
    public void reset() {
        stop();
        elapsedMs = 0;
        notifyTick();
    }

    /**
     * @return {@code true} while the timer is actively counting
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @return elapsed milliseconds since {@link #start()}, or the frozen value after {@link #stop()}
     */
    public long getElapsedMs() {
        if (running) {
            return (System.nanoTime() - startNanos) / 1_000_000L;
        }
        return elapsedMs;
    }

    /**
     * @return elapsed time as {@code mm:ss} (minutes may exceed 59)
     */
    public String formatElapsed() {
        long totalSeconds = getElapsedMs() / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * @return elapsed time as {@code mm:ss.cc} (centiseconds) for victory dialogs
     */
    public String formatElapsedDetailed() {
        long ms = getElapsedMs();
        long totalSeconds = ms / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        long millis = (ms % 1000L) / 10L;
        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }

    /** Invokes {@link #onTick} with the current short format. */
    private void notifyTick() {
        if (onTick != null) {
            onTick.accept(formatElapsed());
        }
    }
}
