package util;

import javafx.application.Platform;
import model.User;

import java.util.Timer;
import java.util.TimerTask;

public final class SessionManager {

    private static final long TIMEOUT_DURATION = 10 * 60 * 1000;

    private static Timer timer;

    private static Runnable onTimeout;

    // Logged-in user
    private static User currentUser;

    private SessionManager() {
    }

    // ===========================
    // USER SESSION
    // ===========================

    public static void login(User user) {

        currentUser = user;

        startSession();

    }

    public static User getCurrentUser() {

        return currentUser;

    }

    public static boolean isLoggedIn() {

        return currentUser != null;

    }

    public static void logout() {

        currentUser = null;

        stopSession();

    }

    // ===========================
    // SESSION TIMEOUT
    // ===========================

    public static void setOnTimeout(Runnable action) {

        onTimeout = action;

    }

    public static void startSession() {

        resetTimer();

    }

    public static void resetTimer() {

        if (timer != null) {

            timer.cancel();

        }

        timer = new Timer(true);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Platform.runLater(() -> {

                    logout();

                    if (onTimeout != null) {

                        onTimeout.run();

                    }

                });

            }

        }, TIMEOUT_DURATION);

    }

    public static void stopSession() {

        if (timer != null) {

            timer.cancel();

            timer = null;

        }

    }

}