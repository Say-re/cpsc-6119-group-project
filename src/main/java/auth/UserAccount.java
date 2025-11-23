package auth;

/**
 * UserAccount Model.
 * Represents a user account in the Candy Store system.
 * Stores user credentials, role information, and recovery options.
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public class UserAccount {
    private String username;
    private String passwordHash;
    private String role; // "admin" or "customer"
    private String email;
    private String recoveryQuestion;
    private String recoveryAnswerHash;

    /**
     * Constructs a new UserAccount with the specified details.
     *
     * @param username The unique username for this account
     * @param passwordHash The hashed password for authentication
     * @param role The user's role ("admin" or "customer")
     * @param email The user's email address
     * @param recoveryQuestion Security question for password recovery
     * @param recoveryAnswerHash Hashed answer to the recovery question
     */
    public UserAccount(String username, String passwordHash, String role, String email,
                       String recoveryQuestion, String recoveryAnswerHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
        this.recoveryQuestion = recoveryQuestion;
        this.recoveryAnswerHash = recoveryAnswerHash;
    }

    /**
     * Gets the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password hash.
     *
     * @return The hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Gets the user's role.
     *
     * @return The role ("admin" or "customer")
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the email address.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the recovery question.
     *
     * @return The security question for password recovery
     */
    public String getRecoveryQuestion() {
        return recoveryQuestion;
    }

    /**
     * Gets the recovery answer hash.
     *
     * @return The hashed answer to the recovery question
     */
    public String getRecoveryAnswerHash() {
        return recoveryAnswerHash;
    }

    /**
     * Sets the password hash.
     *
     * @param passwordHash The new hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Sets the user's role.
     *
     * @param role The new role ("admin" or "customer")
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the email address.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the recovery question.
     *
     * @param recoveryQuestion The new security question
     */
    public void setRecoveryQuestion(String recoveryQuestion) {
        this.recoveryQuestion = recoveryQuestion;
    }

    /**
     * Sets the recovery answer hash.
     *
     * @param recoveryAnswerHash The new hashed recovery answer
     */
    public void setRecoveryAnswerHash(String recoveryAnswerHash) {
        this.recoveryAnswerHash = recoveryAnswerHash;
    }

    /**
     * Returns a string representation of the user account.
     *
     * @return String containing username, role, email, and recovery status
     */
    @Override
    public String toString() {
        return "UserAccount{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", hasRecoveryQuestion=" + (recoveryQuestion != null && !recoveryQuestion.isEmpty()) +
                '}';
    }
}
