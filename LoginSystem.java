/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.part1poe;

/**
 *
 * @author Palesa
 */
import java.util.ArrayList;
import java.util.Scanner;

public class LoginSystem {
    
    public int getUserCount(){
        return userCount;
    }
    // ===========================
    // 1. VARIABLES (DECLARATION)
    // ===========================
    
    // SOURCE:https://youtu.be/RRubcjpTkks?si=af0FOi2lrW17f40P
    
    
    //Arrays with a fixed size
    String[] usernames = new String[10];
    String[] passwords = new String[10];
    
    //Array list
    private ArrayList<String> cellPhone = new ArrayList<>();
    
    int userCount = 0;
    
    //Booleans
    boolean isRunning = true;
    boolean isRegistered;
    
    
    // ============================
    // 2. CONSTRUCTOR
    // ============================
    
    // SOURCE:https://youtu.be/G1Iln3PSrUg?si=fML8-cSn8AQ0bqJc
    
    public LoginSystem(){
        System.out.println(" LoginSystem started...");
        
    }
    
    // ===============================
    // 3.PROGRAM STARTS
    // ===============================
    
    // SOURCE:https://youtu.be/6djggrlkHY8?si=6fG2JghF1sKNeSG_
    
    public static void main(String[] args){
        
        LoginSystem system = new LoginSystem();// constructor runs
        Scanner scanner = new Scanner(System.in);
        
        int choice = 0;
        
        // while llop
        while (system.isRunning){
            
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("Enter your choice:");
            
            
        choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice == 1){
            system.register(scanner);
            
        }
        else if (choice == 2){
            system.login(scanner);
        
        }
        
        else if (choice == 3){
            system.isRunning = false;
            System.out.println("Program ended.");
        }
        else {
            System.out.println("Invalid option.");
        }
      }
    }
 
     //===================================
    // 4. REGISTER METHOD
    // ===================================
    
    
    public void register(Scanner scanner) {
        int count = 0;
        
        if(count >= usernames.length ){
            System.out.println("Array is full");
            return;
        }
    System.out.print("Enter username: ");
     String username = scanner.nextLine();
     
     boolean okUser = username.contains("_");
     
     if(okUser){
         System.out.println("Username must contain _");
         return;
     }
     
     System.out.print("Enter password: ");
     String p = scanner.nextLine();
     
     boolean okPass = p.length()>=4;
     
     if(!okPass){
         System.out.println("Password too short");
         return;
     }
     
     System.out.print("Enter phone number:");
     String phone = scanner.nextLine();
     
  System.out.println("User registered.");
    }

    // =================================
    // 5. LOGIN METHOD
    // =================================
    
    // SOURCE:https://youtu.be/Hiv3gwJC5kw?si=Uvm4KaZ0qz-CxJao
    
   public void login(Scanner scanner) {
    
       System.out.print("Username: ");
       String username = scanner.nextLine();
       
       System.out.print("Password: ");
       String password = scanner.nextLine();
       
       boolean found = false;
        int count = 0;
       
       for (int i = 0; i < count; i++){
           if(usernames[i] != null && passwords[i] != null) {
               if(usernames[i].equals(usernames) && passwords[i].equals(passwords)){
                   found = true;
               }
           }
       }
    
   
   if (found ){
   System.out.println("Login successful");
} else {
   System.out.println("Login failed");
   }
   }
}
   



    
        
    
                
   
            
        
    

