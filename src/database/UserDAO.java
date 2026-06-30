package src.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import src.model.User;

public class UserDAO {

    // REGISTER USER
public static boolean registerUser(
        String username,
        String password,
        String email,
        String phone
) {

    try {

        Connection conn =
            DBConnection.getConnection();

        String checkQuery =
            "SELECT * FROM users WHERE username=?";

        PreparedStatement checkPs =
            conn.prepareStatement(checkQuery);

        checkPs.setString(1, username);

        ResultSet rs =
            checkPs.executeQuery();

        if (rs.next()) {

            return false;
        }

        String query =
            "INSERT INTO users " +
            "(username, password, email, phone) " +
            "VALUES (?, ?, ?, ?)";

        PreparedStatement ps =
            conn.prepareStatement(query);

        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, email);
        ps.setString(4, phone);

        ps.executeUpdate();

        return true;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}

    // LOGIN USER
    public static User loginUser(
            String username,
            String password
    ) {

        try {

            Connection conn = DBConnection.getConnection();

            String query =
                    "SELECT * FROM users " +
                    "WHERE username=? AND password=?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(

                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}