/**
 * UserAccount Model
 * Represents a user account in the Candy Store system
 */
public class UserAccount {
    private String username;
    private String passwordHash;
    private String role; // "admin" or "customer"
    private String email;
    private String recoveryQuestion;
    private String recoveryAnswerHash;

    public UserAccount(String username, String passwordHash, String role, String email,
                       String recoveryQuestion, String recoveryAnswerHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
        this.recoveryQuestion = recoveryQuestion;
        this.recoveryAnswerHash = recoveryAnswerHash;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getRecoveryQuestion() {
        return recoveryQuestion;
    }

    public String getRecoveryAnswerHash() {
        return recoveryAnswerHash;
    }

    // Setters
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRecoveryQuestion(String recoveryQuestion) {
        this.recoveryQuestion = recoveryQuestion;
    }

    public void setRecoveryAnswerHash(String recoveryAnswerHash) {
        this.recoveryAnswerHash = recoveryAnswerHash;
    }

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
