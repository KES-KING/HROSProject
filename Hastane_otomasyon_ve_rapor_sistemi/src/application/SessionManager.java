package application;

public class SessionManager {
    private static int currentUserId;
    private static boolean isAdmin;

    public static void login(int userId, boolean adminStatus) {
        currentUserId = userId;
        isAdmin = adminStatus;
    }

    public static void logout() {
        currentUserId = -1;
        isAdmin = false;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }
}