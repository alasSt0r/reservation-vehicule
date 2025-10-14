package com.example.reservation;

public class Colors {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BOLD = "\u001B[1m";
    private static final String BOLD_RED = "\u001B[1m\u001B[31m";
    private static final String BOLD_GREEN = "\u001B[1m\u001B[32m";

    public Colors() {
    }

    public static String red(String text) {
        return RED + text + RESET;
    }

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    public static String boldRed(String text) {
        return BOLD_RED + text + RESET;
    }

    public static String boldGreen(String text) {
        return BOLD_GREEN + text + RESET;
    }

}
