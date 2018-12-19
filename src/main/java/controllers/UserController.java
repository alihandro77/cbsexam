package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import model.User;
import utils.Hashing;
import utils.Log;

public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {

    Hashing hashwhatever = new Hashing();

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it. - FIXED


    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
                //Hasher her password
            + hashwhatever.HashSalt(user.getPassword()
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }


  public static void deleteUser (int id) {

    //Tjekker efter for at se om der er en forbindelse til databasen

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    //Her kommer der et prepared statement
    String sql = "DELETE FROM user WHERE id=" + id;

    dbCon.deleteUser(sql);
  }

  public static String loginUser(User user) {

    //Tjekker efter for at se om databasen er forbundet

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "SELECT * FROM user where the email=" + user.getEmail() + "AND password" + user.getPassword() + "";

    dbCon.loginUser (sql);

    //Så skaber vi en query
    ResultSet resultSet = dbCon.query(sql);
    User userlogin
    String token = null

    try {
      //Da vi kun har et objekt, er det det eneste vi getter
      if (resultSet.next()) {
        userlogin = new User(
                resultSet.getInt("ID"),
                resultSet.getInt("first_name"),
                resultSet.getInt("last_name"),
                resultSet.getInt("password"),
                resultSet.getInt("email"),

                if (userlogin != null) {
                  try {

                    Algorithm algorithm = Algorithm.HMAC256("Secret");
                    token = JWT.create()
                            .withClaim("UserID", userlogin.getId())
                            .withIssuer("auth0")
                            .sign(algorithm);
                  } catch (JWTCreationException exception) {
                  //Forkert sign konfiguration / kunne altså ikke konvertere claims
                  System.out.println(exception.getMessage());
                } finally {
                  return token;
                  }
                }
      } else {
      System.out.println("There was no user found");
    }
  } catch (SQLException ex) {

    System.out.println(ex.getMessage());
  }
    //Returnerer null

  return "";
}

public static Boolean updateUser(User user, String token) {
  if (dbCon == null) {
    dbCon = new DatabaseController();
  }

  try {
    DecodedJWT jwt = JWT.decode(token);
    int id = jwt.getClaim("UserID").asInt();

    try {
      PreparedStatement updateUser = dbCon.getConnection().prepareStatement("UPDATE USER SET" + "first_name  = ?, last name = ?, password = ?, email = ?, WHERE id = ?");
      updateUser.setString(1, user.getFirstname());
      updateUser.setString(2, user.getLastname());
      updateUser.setString(3, user.getPassword());
      updateUser.setString(4, user.getEmail());
      updateUser.setInt(5, id);
      int rowsaffected = updateUser.executeUpdate();

      if (rowsaffected == 1) {
        return true;

      }

    } catch (SQLException sql) {
      sql.printStackTrace();

    }

  } catch (JWTCreationException exception) {
    exception.printStackTrace();
  }
  return false;
}
}
