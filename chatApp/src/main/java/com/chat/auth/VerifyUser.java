package com.chat.auth;
import java.util.Scanner;

public class VerifyUser {
    Scanner scanner = new Scanner(System.in);
    public User verify() {
        System.out.println("Hi there!! Please login/register yourself to start");
        System.out.println("Are you new User? Enter R else Enter L: ");
        while(true) {
            String method = scanner.nextLine();
            if(method.equals("R")){
                Register register = new Register();
                User user = register.addUser();
                if(user != null) {
                    System.out.println("Welcome!! " + user.getUserName());
                    
                    return user;
                } else {
                    System.out.println("Enter R for Register, L for Login");
                }
            } else if(method.equals("L")) {
                SignIn signIn = new SignIn();
                User user = signIn.verify();
                if(user != null){
                    System.out.println("Welcome!! " + user.getUserName());
                    
                    return user;
                } else {
                    System.out.println("Are you new User? Enter R else Enter L: ");
                }
            } else {
                System.out.println("Invalid Argument!!!");
            }
 
        }
        
    }
}