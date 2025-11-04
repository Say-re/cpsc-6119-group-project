package model;

public class Customer {
    private final String id;
    private String username;
    private String email;

    public Customer(String id, String username, String email) {
        this.id = id; this.username = username; this.email = email;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
