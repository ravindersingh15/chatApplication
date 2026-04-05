package com.chat.auth;

import java.util.Scanner;

import com.chat.api.Authentication;
import com.chat.utils.HashUtil;
import com.chat.utils.InputUtil;

public class SignIn {
    Scanner scanner = new Scanner(System.in);
    public User verify(String hashSalt){
        System.out.print("Enter EmailId: ");
        String EmailId = scanner.nextLine();
        
        String Password = InputUtil.readPassword("Enter Password: ");
        
        // Hash the password with the salt before sending it to validate
        String hashedPassword = HashUtil.hashPassword(Password, hashSalt);
        
        User user = validate(EmailId, hashedPassword);
        if(user != null){
            return user;
        } else {
            System.out.println("Invalid UserName or Password");
            return null;
        }
    }
    private User validate(String EmailId, String Password) {
        Authentication authentication = new Authentication();
        User user = authentication.login(EmailId, Password);
        if(user != null) {
            System.out.println("Login Successful");
            return user;
        } else {
            System.out.println("Login Failed");
            return null;  
        } 
    }
}
