package edu.brown.cs.student.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.brown.cs.student.farm.FarmFile;

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
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_info;");
      prep.executeUpdate();

      prep.close();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_data;");
      prep.executeUpdate();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_inventory;");
      prep.executeUpdate();
      // , PRIMARY KEY(username)
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_info(username text, password text,salt text,email text);");
      prep.executeUpdate();

      prep.close();
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_data(username text, farm blob, new_user integer, friends text, friendspending text);");
      prep.executeUpdate();
      prep.close();
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory(username text, tomatoes integer, corn integer, wheat integer, cotton integer, rice integer, sugar integer,apples integer, pears integer, oranges integer, tangerines integer, bananas integer, strawberries integer, kiwis integer, watermelons integer);");
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
      prep = conn.prepareStatement(
          "INSERT INTO user_data(username, friends, friendspending) VALUES (?, ?,?);");
      prep.setString(1, username);
      prep.setString(2, "");
      prep.setString(3, "");
      prep.addBatch();
      prep.executeBatch();
      prep.close();
      prep = conn.prepareStatement(
          "INSERT INTO user_inventory VALUES (?, ?,?,?, ?,?,?, ?,?,?, ?,?,?,?,?);");
      prep.setString(1, username);
      prep.setInt(2, 0);
      prep.setInt(3, 0);
      prep.setInt(4, 0);
      prep.setInt(5, 0);
      prep.setInt(6, 0);
      prep.setInt(7, 0);
      prep.setInt(8, 0);
      prep.setInt(9, 0);
      prep.setInt(10, 0);
      prep.setInt(11, 0);
      prep.setInt(12, 0);
      prep.setInt(13, 0);
      prep.setInt(14, 0);
      prep.setInt(15, 0);

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

  /**
   * This method is where when a user adds a friend, the friend request will go to
   * the user they want to add and this user's pending list will be updated so
   * that it includes this user.
   *
   * @param usernameToAddToPending the user that is trying to add another user and
   *                               will be added to their list
   * @param userlistbeingupdated   the user whos list is being updated.
   */
  public static void UpdateFriendsPending(String usernameToAddToPending,
      String userlistbeingupdated) {
    PreparedStatement prep;
    String friends = null;
    ResultSet rs = null;
    // getting the old friendlist.
    try {
      prep = conn.prepareStatement("SELECT friendspending FROM user_data WHERE username=?;");
      prep.setString(1, userlistbeingupdated);
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
      friendsList.append(usernameToAddToPending);
      friendsList.append(",");
    } else {
      friendsList.append(friends);
      friendsList.append(usernameToAddToPending);
      friendsList.append(",");
    }

    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_data SET friendspending= ? WHERE username=?;");
      prep.setString(1, friendsList.toString());
      prep.setString(2, userlistbeingupdated);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // This will update the friend list of the other user.

  }

  /**
   * This method will return the pending friend list of a user a string that is
   * separated by commas for username that is in the string.
   *
   * @param username The user for which to get the pending list for.
   * @return
   */
  public static String getFriendsListPending(String username) {
    PreparedStatement prep;
    String friends = null;
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT friendspending FROM user_data WHERE username=?;");
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

  /**
   * This method is where we will update the pending friend list when the user
   * accepts the person that is in their friend list.
   *
   * @param replacement the list of the current pending friendlist as a string.
   * @param user        the user for whom to replace the pending list of.
   */
  public static void UpdateFriendsPendingAfterAdding(String replacement, String user) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_data SET friendspending= ? WHERE username=?;");
      prep.setString(1, replacement);
      prep.setString(2, user);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // This will update the friend list of the other user.

  }

  /**
   * This method will initialize the user farm when they start the game.
   *
   * @param username the user for who we are setting the farm to.
   * @param farm     The farm object and in this case a TestFarm
   */
  public static void initializeFarm(String username, FarmFile farm) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("INSERT INTO user_data(farm) VALUES (?);");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos;
      try {
        oos = new ObjectOutputStream(bos);

        oos.writeObject(farm);
        oos.flush();
        oos.close();
        bos.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      byte[] data = bos.toByteArray();
      prep.setBytes(1, data);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * This method will save the user farm whenever they do something or log out.
   *
   * @param username the user for who we are setting the farm to.
   * @param farm     The farm object and in this case a TestFarm
   */
  public static void saveFarm(String username, FarmFile farm) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_data SET farm= ? WHERE username=?;");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos;
      try {
        oos = new ObjectOutputStream(bos);

        oos.writeObject(farm);
        oos.flush();
        oos.close();
        bos.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      byte[] data = bos.toByteArray();
      prep.setBytes(1, data);
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * This method will load the user farm for when the user logs in.
   *
   * @param username the user for who we are loading the farm for
   * @return Will return the farm file instance.
   */
  public static FarmFile loadFarm(String username) {
    PreparedStatement prep;
    ResultSet rs = null;
    ByteArrayInputStream bais = null;
    ObjectInputStream ins = null;
    FarmFile farmclass = null;
    try {
      prep = conn.prepareStatement("SELECT farm FROM user_data WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        byte[] bytess = rs.getBytes(1);
        if (bytess == null) {
          return null;
        }
        bais = new ByteArrayInputStream(bytess);
      }
      if (bais == null) {
        return farmclass;
      }
      try {
        ins = new ObjectInputStream(bais);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;

      }
      try {
        farmclass = (FarmFile) ins.readObject();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return null;
    }

    return farmclass;

  }

  /**
   * This method will update the inventory of the user.
   *
   * @param username the username for whom to update inventory for
   * @param fruit    a string that represents the fruit name
   * @param number   the number to update the inventory of that fruit to.
   */
  public static void updateInventory(String username, String fruit, int number) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_inventory SET " + fruit + "= ? WHERE username=?;");
      prep.setInt(1, number);
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("ERROR");
    }
    // This will update the friend list of the other user.

  }

}
