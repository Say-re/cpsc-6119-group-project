import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * UserManager
 * Handles user authentication, account creation, and CSV storage
 * Uses SHA-256 with salt for password hashing
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
     * Singleton pattern to ensure single instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Initialize default admin and customer accounts
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
     * Load users from CSV file
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
     * Save users to CSV file
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
     * Generate a random salt
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with salt using SHA-256
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
     * Create a new user account
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
     * Authenticate a user
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
     * Check if username exists
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Get user by username
     */
    public UserAccount getUser(String username) {
        return users.get(username);
    }

    /**
     * Update user password
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
     * Validate recovery answer for password recovery
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
}
