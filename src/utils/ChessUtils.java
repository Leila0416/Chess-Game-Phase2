package utils;

public class ChessUtils {

    public static boolean isValidMoveFormat(String input) {
        return input.matches("^[A-Ha-h][1-8]\\s[A-Ha-h][1-8]$");
    }
}
