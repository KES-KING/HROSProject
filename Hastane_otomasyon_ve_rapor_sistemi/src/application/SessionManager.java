package application;

public class SessionManager {
    private static int currentUserId;
    private static boolean isAdmin;
    
    public static void login(int userId, boolean adminStatus) {
        currentUserId = userId;
        isAdmin = adminStatus;
    }
    
    public static boolean isAdmin() {
        return isAdmin;
    }
    
    public static void logout() {
        currentUserId = 0;
        isAdmin = false;
    }
}