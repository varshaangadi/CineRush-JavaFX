package src.model;

public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;

    public User(
            int id,
            String username,
            String password,
            String email,
            String phone,
            String role
    ) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }
}