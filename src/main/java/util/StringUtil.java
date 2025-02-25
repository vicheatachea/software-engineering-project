package util;

public class StringUtil {
    public static String capitaliseFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
