package configuration;

public class Pref {

    private static String status ="not-active";
    private static int counterId;
    private static int userId = 1;
    private static String prevStatus ="not-active";

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        if(status.equals("active") || status.equals("not-active")){
            Pref.prevStatus = status;
        }
        Pref.status = status;
    }

    public static int getCounterId() {
        return counterId;
    }

    public static void setCounterId(int counterId) {
        Pref.counterId = counterId;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        Pref.userId = userId;
    }

    public static String getPrevStatus() {
        return prevStatus;
    }

    public static void setPrevStatus(String prevStatus) {
        Pref.prevStatus = prevStatus;
    }
}
