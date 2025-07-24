package com.chat.auth;

import java.util.Scanner;

import com.chat.api.Authentication;

public class SignIn {
    Scanner scanner = new Scanner(System.in);
    public User verify(){
        System.out.println("Enter EmailId: ");
        String EmailId = scanner.nextLine();
        System.out.println("Enter Password ");
        String Password = scanner.nextLine();
        User user = validate(EmailId, Password);
        if(user != null){
            // this.register()
            return user;
        } else {
            System.out.println("Invalid UserName or Password");
            return null;
        }
    }
    private User validate(String EmailId, String Password) {
        Authentication authentication = new Authentication();
        System.out.println(EmailId + " " + Password);
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
