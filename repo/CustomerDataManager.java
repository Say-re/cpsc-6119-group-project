package repo;

import model.Customer;
import util.CsvUtil;

import java.io.IOException;
import java.util.*;

public class CustomerDataManager {
    private final String file = "users.csv"; // user_id, username, password_hash, role, email, favorite_color

    public Optional<Customer> findById(String id) {
        try {
            for (String[] row : CsvUtil.read(file)) {
                if (row.length >= 5 && row[0].equals(id)) {
                    return Optional.of(new Customer(row[0], row[1], row[4]));
                }
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    public Optional<Customer> findByUsername(String username) {
        try {
            for (String[] row : CsvUtil.read(file)) {
                if (row.length >= 2 && row[1].equalsIgnoreCase(username)) {
                    String email = (row.length >= 5) ? row[4] : "";
                    return Optional.of(new Customer(row[0], row[1], email));
                }
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }
}
