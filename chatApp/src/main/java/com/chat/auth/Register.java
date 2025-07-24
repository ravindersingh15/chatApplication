package com.chat.auth;

import java.util.Scanner;

import com.chat.api.Authentication;

public class Register {
    public User addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter EmailId: ");
        String EmailId = scanner.nextLine();
        System.out.println("Enter UserName: ");
        String UserName = scanner.nextLine();
        System.out.println("Enter Password(min 4 letters): ");
        String Password = scanner.nextLine();
        System.out.println("Confirm your Password: ");
        String rePassword = scanner.nextLine();
        if(rePassword.equals(Password)){
            User user  = this.register(EmailId, UserName, Password);
            if(user != null) {
                System.out.println("Registration Successful");
                return user;
            } else {
                System.out.println("User already exists with this emailId or registration failed, try login instead.");
                return null;
            }
        } else {
            System.out.println("password doesn't matched!!");
            return null;
        }
    }

    private User register(String EmailId, String UserName, String Password) {
        // Call the API to register the user
       Authentication authentication = new Authentication();
        User user = authentication.register(EmailId, UserName, Password);
        if(user != null) {
            System.out.println("Registration Successful");
            return user;
        } else {
            System.out.println("Registration Failed");
            return null;  
        } 
    }
}
