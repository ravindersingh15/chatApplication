package com.chat.auth;

import java.util.Scanner;

import com.chat.api.Authentication;
import com.chat.utils.HashUtil;
import com.chat.utils.InputUtil;

public class Register {
    public User addUser(String hashSalt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter EmailId: ");
        String EmailId = scanner.nextLine();
        System.out.print("Enter UserName: ");
        String UserName = scanner.nextLine();
        
        String Password = InputUtil.readPassword("Enter Password(min 4 letters): ");
        String rePassword = InputUtil.readPassword("Confirm your Password: ");
        
        if(rePassword.equals(Password)){
            // Hash the password with the salt
            String hashedPassword = HashUtil.hashPassword(Password, hashSalt);
            
            User user  = this.register(EmailId, UserName, hashedPassword);
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
