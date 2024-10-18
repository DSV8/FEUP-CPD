package Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UserManager {
    private static final String USER_DATA_FILE = "users.txt";
    private static final Map<String, User> users = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        loadUsers();
    }

    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    int level = Integer.parseInt(parts[2]);
                    User user = new User(username, password, level);
                    users.put(username, user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUserDataToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getLevel());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static User register(String username, String password) {
        if (users.containsKey(username)) {
            return null;
        }

        User user = new User(username, password, 1);
        addUser(user);

        return user;
    }

    private static void addUser(User user) {
        lock.lock();
        try {
            users.put(user.getUsername(), user);
            saveUserDataToFile(user);
        } finally {
            lock.unlock();
        }
    }
}
