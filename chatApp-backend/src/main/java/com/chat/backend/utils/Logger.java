package com.chat.backend.utils;

public class Logger {
    
    public static void info(String message) {
        System.out.println("[INFO]: " + message);
    }
    
    public static void debug(String message) {
        System.out.println("[DEBUG]: " + message);
    }
    
    public static void error(String message) {
        System.err.println("[ERROR]: " + message);
    }

    public static void error(String message, Throwable t) {
        System.err.println("[ERROR]: " + message);
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }
}
