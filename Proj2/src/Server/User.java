package Server;

public class User {
    private final String username;
    private final String password;
    private int level;

    public User(String username, String password, int level) {
        this.username = username;
        this.password = password;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getLevel() {
        return level;
    }

    public void updateLevel(int delta) {
        this.level += delta;
    }
}