package edu.brown.cs.student.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This will handle all the queries and everything that has to do with the base
 * will be handled here in this class.
 *
 */
public final class FarmProxy {
  private static Connection conn;

  private FarmProxy() {
  }

  /**
   * This method will set up the database connection and will make sure to create
   * the tables if they do not exist within the database.
   */
  public static void setUpDataBase() {
    String urlToDB = "jdbc:sqlite:" + "data/farm_simulator.sqlite3";
    try {
      conn = DriverManager.getConnection(urlToDB);
      PreparedStatement prep;
      // simulator databases
//      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_info;");
//      prep.executeUpdate();
//
//      prep.close();
//      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_data;");
//      prep.executeUpdate();
      // , PRIMARY KEY(username)
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_info(username text, password text,salt text,email text);");
      prep.executeUpdate();

      prep.close();
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_data(username text, farm_data text, new_user integer, friends text, friendspending text);");
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method will return the database connection
   *
   * @return the connection for the database.
   */
  public static Connection getConnection() {
    return conn;
  }

  public static String getUserNameFromDataBase(String username) {
    String nameToReturn = null;
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT username FROM user_info WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        nameToReturn = rs.getString(1);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      return null;
    }
    return nameToReturn;
  }

  /**
   * This method will insert the user information on the sign up when that person
   * has entered valid information.
   *
   * @param username       Take in a string that represents the username.
   * @param hashedpassword Take in string that represents the hashedpassword.
   * @param salt           take in the salt as a string.
   * @param email          take in the email as a string.
   */
  public static void insertUserInfoIntoDatabase(String username, String hashedpassword, String salt,
      String email) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("INSERT INTO user_info VALUES (?, ?, ?, ?);");
      prep.setString(1, username);
      prep.setString(2, hashedpassword);
      prep.setString(3, salt);
      prep.setString(4, email);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
      prep = conn.prepareStatement("INSERT INTO user_data(username, friends) VALUES (?, ?);");
      prep.setString(1, username);
      prep.setString(2, "");
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * This method will get the user information to validate the login.
   *
   * @param username It will take in the username the user has entered as the
   *                 string.
   * @return It will return a stirng array that represents the user information.
   */
  public static String[] getUserInfoFromDataBaseForLogIn(String username) {
    String[] infoToReturn = null;
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT * FROM user_info WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        infoToReturn = new String[4];
        infoToReturn[0] = rs.getString(1);
        infoToReturn[1] = rs.getString(2);
        infoToReturn[2] = rs.getString(3);
        infoToReturn[3] = rs.getString(4);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      return null;
    }
    return infoToReturn;
  }

  /**
   * This method will see if the email the person is trying to make their account
   * with is taken or not.
   *
   * @param email It will take in the email the user is trying to make an account
   *              with.
   * @return It will return null if the email doesn't exist and will return the
   *         email if it does exist.
   */
  public static String getEmailFromDataBase(String email) {
    String nameToReturn = null;
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT username FROM user_info WHERE email=?;");
      prep.setString(1, email);
      rs = prep.executeQuery();
      while (rs.next()) {
        nameToReturn = rs.getString(1);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      return null;
    }
    return nameToReturn;
  }

  /**
   * This method will update the friends list for a single user as a string that
   * represents the list.
   *
   * @param username This will be the username of the current player.
   * @param toAdd    This will be the username to add.
   */
  public static void UpdateFriendsList(String username, String toAdd) {
    PreparedStatement prep;
    String friends = null;
    ResultSet rs = null;
    // getting the old friendlist.
    try {
      prep = conn.prepareStatement("SELECT friends FROM user_data WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        friends = rs.getString(1);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
    }
    StringBuilder friendsList = new StringBuilder();
    // appending to the list in form of a string.
    if (friends.equals("")) {
      friendsList.append(toAdd);
      friendsList.append(",");
    } else {
      friendsList.append(friends);
      friendsList.append(toAdd);
      friendsList.append(",");
    }

    try {
      // update the string that represents the friend list.
      prep = conn.prepareStatement("UPDATE user_data SET friends= ? WHERE username=?;");
      prep.setString(1, friendsList.toString());
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // This will update the friend list of the other user.

  }

  /**
   * This method will retrieve the friends list as a string that will be parsed by
   * the javascript code in order to output the friends in a list.
   *
   * @param username It takes in the user for which to find the friend list for.
   * @return It will return the friends list as string.
   */
  public static String getFriendsList(String username) {
    PreparedStatement prep;
    String friends = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT friends FROM user_data WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        friends = rs.getString(1);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      return null;
    }
    return friends;
  }

}
