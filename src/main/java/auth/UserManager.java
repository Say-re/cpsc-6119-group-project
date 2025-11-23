package auth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * UserManager - Singleton class for managing user accounts.
 * Handles user authentication, account creation, and CSV storage.
 * Uses SHA-256 with salt for password hashing.
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public class UserManager {
    private static final String CSV_FILE = "src/main/java/data/users.csv";
    private static final String CSV_HEADER = "username,passwordHash,salt,role,email,recoveryQuestion,recoveryAnswerHash,answerSalt";
    private static UserManager instance;

    private Map<String, UserAccount> users;
    private Map<String, String> salts; // Store password salts separately
    private Map<String, String> answerSalts; // Store recovery answer salts separately

    private UserManager() {
        users = new HashMap<>();
        salts = new HashMap<>();
        answerSalts = new HashMap<>();
        loadUsers();
        initializeDefaultAccounts();
    }

    /**
     * Singleton pattern to ensure single instance.
     *
     * @return The singleton UserManager instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Initialize default admin and customer accounts.
     * Only creates defaults if no users exist in the system.
     */
    private void initializeDefaultAccounts() {
        // Only create defaults if no users exist
        if (users.isEmpty()) {
            try {
                createUser("admin", "admin123", "admin", "admin@sweetfactory.com",
                          "What is your favorite book?", "1984");
                createUser("customer", "customer123", "customer", "customer@sweetfactory.com",
                          "What was your first pet's name?", "Fluffy");
                System.out.println("Default accounts created:");
                System.out.println("  Admin - username: admin, password: admin123");
                System.out.println("  Customer - username: customer, password: customer123");
            } catch (Exception e) {
                System.err.println("Error creating default accounts: " + e.getMessage());
            }
        }
    }

    /**
     * Load users from CSV file.
     * Reads the users.csv file and populates the users map.
     */
    private void loadUsers() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to preserve empty strings
                if (parts.length >= 8) {
                    String username = parts[0];
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String role = parts[3];
                    String email = parts[4];
                    String recoveryQuestion = parts[5];
                    String recoveryAnswerHash = parts[6];
                    String answerSalt = parts[7];

                    UserAccount account = new UserAccount(username, passwordHash, role, email,
                                                          recoveryQuestion, recoveryAnswerHash);
                    users.put(username, account);
                    salts.put(username, salt);
                    answerSalts.put(username, answerSalt);
                } else if (parts.length >= 5) {
                    // Backward compatibility for old format without recovery questions
                    String username = parts[0];
                    String passwordHash = parts[1];
                    String salt = parts[2];
                    String role = parts[3];
                    String email = parts[4];

                    UserAccount account = new UserAccount(username, passwordHash, role, email, "", "");
                    users.put(username, account);
                    salts.put(username, salt);
                    answerSalts.put(username, "");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Save users to CSV file.
     * Writes all user accounts to the users.csv file.
     */
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println(CSV_HEADER);
            for (UserAccount account : users.values()) {
                String salt = salts.get(account.getUsername());
                String answerSalt = answerSalts.get(account.getUsername());
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    account.getUsername(),
                    account.getPasswordHash(),
                    salt,
                    account.getRole(),
                    account.getEmail(),
                    account.getRecoveryQuestion() != null ? account.getRecoveryQuestion() : "",
                    account.getRecoveryAnswerHash() != null ? account.getRecoveryAnswerHash() : "",
                    answerSalt != null ? answerSalt : ""
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Generate a random salt for password hashing.
     *
     * @return Base64-encoded random salt
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with salt using SHA-256.
     *
     * @param password The plaintext password
     * @param salt The salt to use for hashing
     * @return Base64-encoded hashed password
     */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Create a new user account.
     *
     * @param username The username for the new account
     * @param password The plaintext password
     * @param role The user role ("admin" or "customer")
     * @param email The email address
     * @param recoveryQuestion Security question for password recovery
     * @param recoveryAnswer Answer to the security question
     * @return true if user was created successfully, false if username already exists
     */
    public boolean createUser(String username, String password, String role, String email,
                             String recoveryQuestion, String recoveryAnswer) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }

        String salt = generateSalt();
        String passwordHash = hashPassword(password, salt);

        String answerSalt = generateSalt();
        String recoveryAnswerHash = hashPassword(recoveryAnswer.toLowerCase().trim(), answerSalt);

        UserAccount account = new UserAccount(username, passwordHash, role, email,
                                             recoveryQuestion, recoveryAnswerHash);
        users.put(username, account);
        salts.put(username, salt);
        answerSalts.put(username, answerSalt);

        saveUsers();
        return true;
    }

    /**
     * Authenticate a user with username and password.
     *
     * @param username The username to authenticate
     * @param password The plaintext password to verify
     * @return UserAccount object if authentication successful, null otherwise
     */
    public UserAccount authenticate(String username, String password) {
        UserAccount account = users.get(username);
        if (account == null) {
            return null; // User not found
        }

        String salt = salts.get(username);
        String passwordHash = hashPassword(password, salt);

        if (passwordHash.equals(account.getPasswordHash())) {
            return account; // Authentication successful
        }

        return null; // Invalid password
    }

    /**
     * Check if username exists.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Get user by username.
     *
     * @param username The username to retrieve
     * @return UserAccount object if found, null otherwise
     */
    public UserAccount getUser(String username) {
        return users.get(username);
    }

    /**
     * Update user password.
     *
     * @param username The username whose password to update
     * @param newPassword The new plaintext password
     * @return true if password was updated successfully, false if user not found
     */
    public boolean updatePassword(String username, String newPassword) {
        UserAccount account = users.get(username);
        if (account == null) {
            return false;
        }

        String salt = generateSalt();
        String passwordHash = hashPassword(newPassword, salt);

        account.setPasswordHash(passwordHash);
        salts.put(username, salt);

        saveUsers();
        return true;
    }

    /**
     * Validate recovery answer for password recovery.
     *
     * @param username The username to validate
     * @param answer The plaintext answer to verify
     * @return true if answer is correct, false otherwise
     */
    public boolean validateRecoveryAnswer(String username, String answer) {
        UserAccount account = users.get(username);
        if (account == null || account.getRecoveryAnswerHash() == null || account.getRecoveryAnswerHash().isEmpty()) {
            return false;
        }

        String answerSalt = answerSalts.get(username);
        if (answerSalt == null) {
            return false;
        }

        String answerHash = hashPassword(answer.toLowerCase().trim(), answerSalt);
        return answerHash.equals(account.getRecoveryAnswerHash());
    }

    /**
     * Get all user accounts.
     *
     * @return List of all UserAccount objects
     * @author Travis Dagostino
     * @since 2025-11-22
     */
    public List<UserAccount> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Update user role (admin or customer).
     *
     * @param username The username of the account to update
     * @param newRole The new role ("admin" or "customer")
     * @return true if role was updated successfully, false if user not found
     * @author Travis Dagostino
     * @since 2025-11-22
     */
    public boolean updateUserRole(String username, String newRole) {
        UserAccount account = users.get(username);
        if (account == null) {
            return false;
        }

        // Validate role
        if (!newRole.equals("admin") && !newRole.equals("customer")) {
            return false;
        }

        account.setRole(newRole);
        saveUsers();
        return true;
    }
}
