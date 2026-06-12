package com.mycompany.messageapp2;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class MessageApp2 {
    
    // Arrays to store users
    private String[] username = new String[10];
    private String[] password = new String[10];
    private String[] cellPhoneNumber = new String[10];
    private int userCount = 0;

    // Login state
    private boolean isRunning = true;
    private boolean isLoggedIn = false;
    
    // POE PART3 - Arrays to store message data
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> disregardedMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<Integer> messageIds = new ArrayList<>();
    
    private static int nextId = 1;
    private static final String STORAGE_FILE = "stored_messages.txt";
    
    // Scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        MessageApp2 app = new MessageApp2();
        
        System.out.println("====================================================");
        System.out.println("         WELCOME TO THE MESSAGE MANAGEMENT SYSTEM   ");
        System.out.println("====================================================");
        
        // Load existing stored messages from file
        loadStoredMessagesFromFile();
        
        // If no data exists, load default test data
        if (sentMessages.isEmpty() && storedMessages.isEmpty() && disregardedMessages.isEmpty()) {
            System.out.println("\nNo existing data found. Loading default test data...");
            loadDefaultTestData();
        } else {
            System.out.println("\nData loaded from file successfully!");
            System.out.println("- Sent Messages: " + sentMessages.size());
            System.out.println("- Stored Messages: " + storedMessages.size());
            System.out.println("- Disregarded Messages: " + disregardedMessages.size());
        }
        
        // Show login/register menu first
        app.showMainMenu();
    }
    
    // Main menu for login/register
    public void showMainMenu() {
        while (isRunning) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        register();
                        break;
                    case 2:
                        loginUser();
                        if (isLoggedIn) {
                            showMessageManagementMenu();
                        }
                        break;
                    case 3:
                        System.out.println("Exiting program...");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } else {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
    
    // Message Management Menu (after login)
    public void showMessageManagementMenu() {
        int choice;
        do {
            System.out.println("\n============================================================");
            System.out.println("                 MESSAGE MANAGEMENT SYSTEM                    ");
            System.out.println("==============================================================");
            System.out.println("   1. Display Sent Messages                                   ");
            System.out.println("   2. Display Disregarded Messages                            ");
            System.out.println("   3. Display Stored Messages                                 ");
            System.out.println("   4. Stored Messages Options                                 ");
            System.out.println("   5. Load Default Test Data                                  ");
            System.out.println("   6. Add New Message                                         ");
            System.out.println("   7. Logout                                                  ");
            System.out.print("Enter your choice: ");
            
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }
            
            switch (choice) {
                case 1:
                    displaySentMessages();
                    break;
                case 2:
                    displayDisregardedMessages();
                    break;
                case 3:
                    displayStoredMessages();
                    break;
                case 4:
                    showStoredMessagesMenu();
                    break;
                case 5:
                    loadDefaultTestData();
                    break;
                case 6:
                    addNewMessage();
                    break;
                case 7:
                    System.out.println("\nLogging out...");
                    isLoggedIn = false;
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }
    
    // Message class to represent a message
    static class Message {
        int id;
        String recipient;
        String message;
        String flag;
        String messageHash;
        
        public Message(int id, String recipient, String message, String flag) {
            this.id = id;
            this.recipient = recipient;
            this.message = message;
            this.flag = flag;
            this.messageHash = generateHash(message);
        }
        
        public String toFileString() {
            return id + "|" + recipient + "|" + message + "|" + flag + "|" + messageHash;
        }
        
        public static Message fromFileString(String line) {
            String[] parts = line.split("\\|");
            if (parts.length == 5) {
                try {
                    return new Message(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return "Message ID: " + id + 
                   "\nRecipient: " + recipient + 
                   "\nMessage: " + message + 
                   "\nFlag: " + flag + 
                   "\nMessage Hash: " + messageHash + "\n";
        }
    }
    
    // Generate hash for a message
    private static String generateHash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(message.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(message.hashCode());
        }
    }
    
    // File operations
    private static void saveStoredMessagesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STORAGE_FILE))) {
            for (Message msg : storedMessages) {
                writer.println(msg.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving stored messages: " + e.getMessage());
        }
    }
    
    private static void loadStoredMessagesFromFile() {
        File file = new File(STORAGE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(STORAGE_FILE))) {
                String line;
                storedMessages.clear();
                while ((line = reader.readLine()) != null) {
                    Message msg = Message.fromFileString(line);
                    if (msg != null) {
                        storedMessages.add(msg);
                        if (msg.id >= nextId) {
                            nextId = msg.id + 1;
                        }
                        if (!messageHashes.contains(msg.messageHash)) {
                            messageHashes.add(msg.messageHash);
                        }
                        if (!messageIds.contains(msg.id)) {
                            messageIds.add(msg.id);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading stored messages: " + e.getMessage());
            }
        }
    }
    
    // Load default test data based on assignment requirements
    public static void loadDefaultTestData() {
        // Clear existing data
        sentMessages.clear();
        storedMessages.clear();
        disregardedMessages.clear();
        messageHashes.clear();
        messageIds.clear();
        nextId = 1;
        
        // Test Data Message 1
        Message msg1 = new Message(nextId++, "+27834557896", "Did you get the cake?", "Sent");
        sentMessages.add(msg1);
        
        // Test Data Message 2
        Message msg2 = new Message(nextId++, "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
        storedMessages.add(msg2);
        
        // Test Data Message 3
        Message msg3 = new Message(nextId++, "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        disregardedMessages.add(msg3);
        
        // Test Data Message 4
        Message msg4 = new Message(nextId++, "0838884567", "It is dinner time!", "Sent");
        sentMessages.add(msg4);
        
        // Additional message
        Message msg5 = new Message(nextId++, "+27838884567", "Ok, I am leaving without you.", "Stored");
        storedMessages.add(msg5);
        
        // Update hashes and IDs arrays
        for (Message msg : sentMessages) {
            messageHashes.add(msg.messageHash);
            messageIds.add(msg.id);
        }
        for (Message msg : storedMessages) {
            messageHashes.add(msg.messageHash);
            messageIds.add(msg.id);
        }
        for (Message msg : disregardedMessages) {
            messageHashes.add(msg.messageHash);
            messageIds.add(msg.id);
        }
        
        saveStoredMessagesToFile();
        
        System.out.println("Default test data loaded successfully!");
        System.out.println("- Sent Messages: " + sentMessages.size());
        System.out.println("- Stored Messages: " + storedMessages.size());
        System.out.println("- Disregarded Messages: " + disregardedMessages.size());
    }
    
    // a. Display sender and recipient of all stored messages
    public static void displayStoredMessagesSenderAndRecipient() {
        System.out.println("\n=== Stored Messages - Sender & Recipient ===");
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        for (Message msg : storedMessages) {
            System.out.println("Sender: User");
            System.out.println("Recipient: " + msg.recipient);
            System.out.println("Message: " + msg.message);
            System.out.println("Message Hash: " + msg.messageHash);
            System.out.println("------------------------");
        }
    }
    
    // b. Display longest stored message
    public static void displayLongestStoredMessage() {
        System.out.println("\n=== Longest Stored Message ===");
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        
        Message longest = storedMessages.stream()
            .max(Comparator.comparingInt(m -> m.message.length()))
            .orElse(null);
        
        if (longest != null) {
            System.out.println("The longest stored message is:");
            System.out.println(longest.toString());
        }
    }
    
    // c. Search for a message ID
    public static void searchByMessageID() {
        System.out.print("\nEnter Message ID to search: ");
        if (scanner.hasNextInt()) {
            int id = scanner.nextInt();
            scanner.nextLine();
            
            Message found = findMessageById(id);
            
            if (found != null) {
                System.out.println("\n=== Message Found ===");
                System.out.println("Recipient: " + found.recipient);
                System.out.println("Message: " + found.message);
                System.out.println("Flag: " + found.flag);
                System.out.println("Message Hash: " + found.messageHash);
                System.out.println("Message ID: " + found.id);
            } else {
                System.out.println("No message found with ID: " + id);
            }
        } else {
            System.out.println("Invalid input. Please enter a valid ID number.");
            scanner.nextLine();
        }
    }
    
    private static Message findMessageById(int id) {
        for (Message msg : sentMessages) {
            if (msg.id == id) return msg;
        }
        for (Message msg : storedMessages) {
            if (msg.id == id) return msg;
        }
        for (Message msg : disregardedMessages) {
            if (msg.id == id) return msg;
        }
        return null;
    }
    
    // d. Search all messages for a recipient
    public static void searchMessagesByRecipient() {
        System.out.print("\nEnter recipient number to search: ");
        String recipient = scanner.nextLine();
        
        List<Message> messagesForRecipient = new ArrayList<>();
        messagesForRecipient.addAll(sentMessages.stream()
            .filter(m -> m.recipient.equals(recipient))
            .collect(Collectors.toList()));
        messagesForRecipient.addAll(storedMessages.stream()
            .filter(m -> m.recipient.equals(recipient))
            .collect(Collectors.toList()));
        messagesForRecipient.addAll(disregardedMessages.stream()
            .filter(m -> m.recipient.equals(recipient))
            .collect(Collectors.toList()));
        
        if (messagesForRecipient.isEmpty()) {
            System.out.println("No messages found for recipient: " + recipient);
        } else {
            System.out.println("\n=== Messages for recipient " + recipient + " ===");
            for (Message msg : messagesForRecipient) {
                System.out.println("- " + msg.message);
                System.out.println("  (ID: " + msg.id + ", Hash: " + msg.messageHash + ")");
            }
        }
    }
    
    // e. Delete a message using hash
    public static void deleteMessageByHash() {
        System.out.print("\nEnter message hash to delete: ");
        String hash = scanner.nextLine();
        
        Message toDelete = null;
        for (Message msg : storedMessages) {
            if (msg.messageHash.equals(hash)) {
                toDelete = msg;
                break;
            }
        }
        
        if (toDelete != null) {
            storedMessages.remove(toDelete);
            messageHashes.remove(hash);
            messageIds.remove(Integer.valueOf(toDelete.id));
            saveStoredMessagesToFile();
            System.out.println("Message: \"" + toDelete.message + "\" successfully deleted.");
            System.out.println("Message hash: " + hash);
        } else {
            System.out.println("No message found with hash: " + hash);
        }
    }
    
    // f. Display full report
    public static void displayStoredMessagesReport() {
        System.out.println("\n========================================================================");
        System.out.println("                              STORED MESSAGES REPORT                      ");
        System.out.println("========================================================================");
        
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        
        System.out.printf("%-20s %-20s %-40s %-10s%n", "Message Hash", "Recipient", "Message", "ID");
        System.out.println(new String(new char[95]).replace('\0', '-'));
        
        for (Message msg : storedMessages) {
            String shortMessage = msg.message.length() > 35 ? msg.message.substring(0, 32) + "..." : msg.message;
            System.out.printf("%-20s %-20s %-40s %-10d%n", 
                msg.messageHash, msg.recipient, shortMessage, msg.id);
        }
        
        System.out.println(new String(new char[95]).replace('\0', '-'));
        System.out.println("Total stored messages: " + storedMessages.size());
        System.out.println("Storage file: " + STORAGE_FILE);
    }
    
    // Display methods
    public static void displaySentMessages() {
        System.out.println("\n=== Sent Messages ===");
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages found.");
            return;
        }
        for (Message msg : sentMessages) {
            System.out.println(msg);
            System.out.println("------------------------");
        }
    }
    
    public static void displayDisregardedMessages() {
        System.out.println("\n=== Disregarded Messages ===");
        if (disregardedMessages.isEmpty()) {
            System.out.println("No disregarded messages found.");
            return;
        }
        for (Message msg : disregardedMessages) {
            System.out.println(msg);
            System.out.println("------------------------");
        }
    }
    
    public static void displayStoredMessages() {
        System.out.println("\n=== Stored Messages ===");
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        for (Message msg : storedMessages) {
            System.out.println(msg);
            System.out.println("------------------------");
        }
    }
    
    public static void addNewMessage() {
        System.out.println("\n=== Add New Message ===");
        
        System.out.print("Enter recipient: ");
        String recipient = scanner.nextLine();
        System.out.print("Enter message: ");
        String message = scanner.nextLine();
        System.out.print("Enter flag (Sent/Stored/Disregard): ");
        String flag = scanner.nextLine();
        
        Message newMsg = new Message(nextId++, recipient, message, flag);
        
        switch (flag.toLowerCase()) {
            case "sent":
                sentMessages.add(newMsg);
                break;
            case "stored":
                storedMessages.add(newMsg);
                break;
            case "disregard":
                disregardedMessages.add(newMsg);
                break;
            default:
                System.out.println("Unknown flag: " + flag);
                return;
        }
        messageHashes.add(newMsg.messageHash);
        messageIds.add(newMsg.id);
        saveStoredMessagesToFile();
        
        System.out.println("Message added successfully!");
        System.out.println("Message ID: " + newMsg.id);
        System.out.println("Message Hash: " + newMsg.messageHash);
    }
    
    private static void showStoredMessagesMenu() {
        String option;
        
        do {
            System.out.println("\n===========================================================");
            System.out.println("                   STORED MESSAGES OPTIONS                   ");
            System.out.println("=============================================================");
            System.out.println("   a. Display sender and recipient of all stored messages    ");
            System.out.println("   b. Display the longest stored message                     ");
            System.out.println("   c. Search for a message ID                                ");
            System.out.println("   d. Search for all messages for a recipient                ");
            System.out.println("   e. Delete a message using the message hash                ");
            System.out.println("   f. Display full report of all stored messages             ");
            System.out.println("   g. Back to main menu                                      ");
            System.out.print("Enter your choice: ");
            
            option = scanner.nextLine().toLowerCase();
            
            switch (option) {
                case "a":
                    displayStoredMessagesSenderAndRecipient();
                    break;
                case "b":
                    displayLongestStoredMessage();
                    break;
                case "c":
                    searchByMessageID();
                    break;
                case "d":
                    searchMessagesByRecipient();
                    break;
                case "e":
                    deleteMessageByHash();
                    break;
                case "f":
                    displayStoredMessagesReport();
                    break;
                case "g":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (true);
    }
    
    // ================= REGISTER AND LOGIN METHODS =================
    
    public void register() {
        if (userCount >= username.length) {
            System.out.println("User limit reached.");
            return;
        }

        System.out.print("Enter username (must contain _ and max 5 chars): ");
        String tempUsername = scanner.nextLine().trim();

        if (!checkUsername(tempUsername)) {
            System.out.println("Username is not correctly formatted.");
            System.out.println("Username must contain '_' and be maximum 5 characters long.");
            return;
        }

        for (int i = 0; i < userCount; i++) {
            if (username[i].equalsIgnoreCase(tempUsername)) {
                System.out.println("Username already exists.");
                return;
            }
        }

        System.out.print("Enter password (min 8 chars, 1 capital, 1 digit, 1 special char): ");
        String tempPassword = scanner.nextLine();

        if (!checkPasswordComplexity(tempPassword)) {
            System.out.println("Password is not correctly formatted.");
            System.out.println("Password must be at least 8 characters long, contain a capital letter, a digit, and a special character.");
            return;
        }

        System.out.print("Enter SA phone number (+27xxxxxxxxx): ");
        String tempCell = scanner.nextLine().trim();

        if (!checkCellPhoneNumber(tempCell)) {
            System.out.println("Invalid cell phone number.");
            System.out.println("Phone number must start with +27 followed by 9 digits.");
            return;
        }

        username[userCount] = tempUsername;
        password[userCount] = tempPassword;
        cellPhoneNumber[userCount] = tempCell;
        userCount++;

        System.out.println("Registration successful!");
    }

    public void loginUser() {
        if (userCount == 0) {
            System.out.println("No users registered. Please register first.");
            return;
        }

        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean success = false;

        for (int i = 0; i < userCount; i++) {
            if (username[i].equals(loginUsername) && password[i].equals(loginPassword)) {
                success = true;
                isLoggedIn = true;
                System.out.println("\nWelcome back " + loginUsername + "!");
                System.out.println("Login successful!");
                break;
            }
        }

        if (!success) {
            System.out.println("Username or password incorrect.");
            isLoggedIn = false;
        }
    }

    public boolean checkUsername(String username) {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null) return false;
        String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        return Pattern.matches(pattern, password);
    }

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) return false;
        String pattern = "^\\+27[0-9]{9}$";
        return Pattern.matches(pattern, cellPhoneNumber);
    }
}