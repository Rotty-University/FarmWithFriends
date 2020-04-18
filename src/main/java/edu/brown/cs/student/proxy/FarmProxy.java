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
          "CREATE TABLE IF NOT EXISTS user_info(username text, password text,salt text);");
      prep.executeUpdate();

      prep.close();
      prep = conn
          .prepareStatement("CREATE TABLE IF NOT EXISTS user_data(username text, farm_data text);");
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

  public static void insertUserInfoIntoDatabase(String username, String hashedpassword,
      String salt) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("INSERT INTO user_info VALUES (?, ?, ?);");
      prep.setString(1, username);
      prep.setString(2, hashedpassword);
      prep.setString(3, salt);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static String[] getUserInfoFromDataBaseForLogIn(String username) {
    String[] infoToReturn = null;
    PreparedStatement prep = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT * FROM user_info WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        infoToReturn = new String[3];
        infoToReturn[0] = rs.getString(1);
        infoToReturn[1] = rs.getString(2);
        infoToReturn[2] = rs.getString(3);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      return null;
    }
    return infoToReturn;
  }

}
