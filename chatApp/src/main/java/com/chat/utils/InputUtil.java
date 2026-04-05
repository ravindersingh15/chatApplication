package com.chat.utils;

import java.io.Console;

public class InputUtil {

    public static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword(prompt);
            return new String(passwordChars);
        } else {
            System.out.println(prompt);
            return new java.util.Scanner(System.in).nextLine();
        }
    }
}