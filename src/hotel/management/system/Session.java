package hotel.management.system;

public class Session {
    public static String loggedNIC = null;
    public static String loggedName = null;
    public static String loggedRole = null;
    
    public static void clear() {
        loggedNIC = null;
        loggedName = null;
        loggedRole = null;
    }
}