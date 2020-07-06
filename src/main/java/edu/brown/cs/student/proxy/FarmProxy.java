package edu.brown.cs.student.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.jzhang30.utils.Constants;
import edu.brown.cs.jzhang30.utils.DatabaseUtils;
import edu.brown.cs.student.farm.Crop;
import edu.brown.cs.student.farm.FarmFile;
import edu.brown.cs.student.farm.FarmLand;

/**
 * This will handle all the queries and everything that has to do with the base
 * will be handled here in this class.
 *
 */
public final class FarmProxy {
  private static Connection conn;
  private static LoadingCache<String, Object[]> cropInfoCache;
  private static final int CACHE_SIZE = 3;

  private FarmProxy() {
  }

  /**
   * This method will set up the database connection and will make sure to create
   * the tables if they do not exist within the database.
   *
   * @param path the path to the database.
   */
  public static void setUpDataBase(String path) {
    String urlToDB = "jdbc:sqlite:" + path;
    try {
      conn = DriverManager.getConnection(urlToDB);
//      dropTables();
      PreparedStatement prep;
////      // simulator databases
      prep = conn
          .prepareStatement("CREATE TABLE IF NOT EXISTS user_info(username text, password text,"
              + "salt text,email text);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_data(username text, farm blob, new_user integer,"
              + " friends text, friendspending text, mapid integer, isNewUser text"
              + ", row int, col int, balance integer, shortcutTools blob);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_crops(username text, tomato integer, "
              + "corn integer, wheat integer, cotton integer, rice integer, sugar integer,"
              + "apple integer, pear integer, orange integer, tangerine integer, "
              + "banana integer, strawberry integer, kiwi integer, watermelon integer,"
              + " avocado integer, lettuce integer, potato integer, cucumber integer, "
              + "carrot integer, greenbean integer, cherry integer, grape integer, "
              + "lemon integer, papaya integer, peach integer, pineapple integer, "
              + "pomegranate integer, cabbage int, kale int, peanut int, pumpkin int, "
              + "broccoli int, lavendar integer, rosemary integer, demo_crop integer, demo_crop2 integer);");
      prep.executeUpdate();
      prep.close();
      // --------------------------------------------------------------------------------------------

      // User tools tables
      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_plow(username text, defaultPlough integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_seeds(username text, tomato integer, "
              + "corn integer, wheat integer, cotton integer, rice integer, sugar integer,"
              + "apple integer, pear integer, orange integer, tangerine integer, "
              + "banana integer, strawberry integer, kiwi integer, watermelon integer,"
              + " avocado integer, lettuce integer, potato integer, cucumber integer, "
              + "carrot integer, greenbean integer, cherry integer, grape integer, "
              + "lemon integer, papaya integer, peach integer, pineapple integer, "
              + "pomegranate integer, cabbage int, kale int, peanut int, pumpkin int, "
              + "broccoli int, lavendar integer, rosemary integer, demo_crop integer, demo_crop2 integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_water(username text, defaultWaterCan integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_cure(username text, defaultTerminator integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_harvest(username text, defaultSickle integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_inventory_tools_steal(username text, defaultStealingHand integer);");
      prep.executeUpdate();
      prep.close();
      // --------------------------------------------------------------------------------------------

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS user_maps(mapid integer, mapdata text, free_space integer);");
      prep.executeUpdate();
      prep.close();

      prep = conn.prepareStatement(
          "CREATE TABLE IF NOT EXISTS trading_center(trader text, crop_sell text, quant_sell text"
              + ", crop_buy text, quant_buy text);");
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    setCaches();
  }

  public static void dropTables() {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_info;");
      prep.executeUpdate();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_data;");
      prep.executeUpdate();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_inventory_crops;");
      prep.executeUpdate();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_inventory_seeds;");
      prep.executeUpdate();
      prep = conn.prepareStatement("DROP TABLE IF EXISTS user_maps;");
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /**
   * This method will return the database connection.
   *
   * @return the connection for the database.
   */
  public static Connection getConnection() {
    return conn;
  }

  /**
   * This method sets the static connection variable in the proxy class.
   *
   * @param connection the connection to set the connection variable of this
   *                   class.
   */
  public static void setConnection(Connection connection) {
    conn = connection;
  }

  /**
   * This method will get the username from the database and see if this username
   * exists. It will be used for login and account creation.
   *
   * @param username The passed in username as a string.
   * @return The same username if it exists null if it doesnt.
   */
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
   *
   * This method will insert the user information on the sign up when that person
   * has entered valid information.
   *
   * @param username       Take in a string that represents the username.
   * @param hashedpassword Take in string that represents the hashedpassword.
   * @param salt           take in the salt as a string.
   * @param email          take in the email as a string.
   * @param mapid          The id of the map the user belongs to.
   * @param newUser        whether or not the user is a new user is not. Set it to
   *                       "true".
   */
  public static void insertUserInfoIntoDatabase(String username, String hashedpassword, String salt,
      String email, int mapid, String newUser) {
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

      // init user data
      prep = conn.prepareStatement("INSERT INTO user_data(username, friends, friendspending, "
          + "mapid, isNewUser, row, col, balance, shortcutTools) VALUES (?, ?,?,?,?,?,?,?,?);");

      prep.setString(1, username);
      prep.setString(2, "");
      prep.setString(3, "");
      prep.setInt(4, mapid);
      prep.setString(5, newUser);
      prep.setInt(6, -1);
      prep.setInt(7, -1);
      prep.setInt(8, 0);
      prep.setBytes(9, DatabaseUtils.convertToByteArray(Constants.DEFAULT_SHORTCUT_TOOLS));
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      // init harvested crop value
      prep = conn.prepareStatement(
          "INSERT INTO user_inventory_crops VALUES (?,?,?, ?,?,?, ?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?, ?,?,"
              + "?, ?,?,?,?,?,?,?,?,?,?,?,?);");
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
      prep.setInt(16, 0);
      prep.setInt(17, 0);
      prep.setInt(18, 0);
      prep.setInt(19, 0);
      prep.setInt(20, 0);
      prep.setInt(21, 0);
      prep.setInt(22, 0);
      prep.setInt(23, 0);
      prep.setInt(24, 0);
      prep.setInt(25, 0);
      prep.setInt(26, 0);
      prep.setInt(27, 0);
      prep.setInt(28, 0);
      prep.setInt(29, 0);
      prep.setInt(30, 0);
      prep.setInt(31, 0);
      prep.setInt(32, 0);
      prep.setInt(33, 0);
      prep.setInt(34, 0);
      prep.setInt(35, 0);
      prep.setInt(36, 0);
      prep.setInt(37, 0);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      // init default TOOL values
      prep = conn.prepareStatement(
          "INSERT INTO user_inventory_tools_seeds VALUES (?,?,?, ?,?,?, ?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?, ?,?,"
              + "?, ?,?,?,?,?,?,?,?,?,?,?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
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
      prep.setInt(16, 0);
      prep.setInt(17, 0);
      prep.setInt(18, 0);
      prep.setInt(19, 0);
      prep.setInt(20, 0);
      prep.setInt(21, 0);
      prep.setInt(22, 0);
      prep.setInt(23, 0);
      prep.setInt(24, 0);
      prep.setInt(25, 0);
      prep.setInt(26, 0);
      prep.setInt(27, 0);
      prep.setInt(28, 0);
      prep.setInt(29, 0);
      prep.setInt(30, 0);
      prep.setInt(31, 0);
      prep.setInt(32, 0);
      prep.setInt(33, 0);
      prep.setInt(34, 0);
      prep.setInt(35, 0);
      prep.setInt(36, 0);
      prep.setInt(37, 0);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      prep = conn.prepareStatement("INSERT INTO user_inventory_tools_cure VALUES (?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      prep = conn.prepareStatement("INSERT INTO user_inventory_tools_harvest VALUES (?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      prep = conn.prepareStatement("INSERT INTO user_inventory_tools_plow VALUES (?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      prep = conn.prepareStatement("INSERT INTO user_inventory_tools_steal VALUES (?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
      prep.addBatch();
      prep.executeBatch();
      prep.close();

      prep = conn.prepareStatement("INSERT INTO user_inventory_tools_water VALUES (?,?);");
      prep.setString(1, username);
      prep.setInt(2, 999);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      System.err.println("ERROR: Can't insert into the database.");
    } catch (IOException e) {
      System.out.println("ERROR: Serialization error.");
    }
  }

  /**
   * This method will get the user information to validate the login.
   *
   * @param username It will take in the username the user has entered as the
   *                 string.
   * @return It will return a string array that represents the user information.
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
  public static void updateFriendsList(String username, String toAdd) {
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
      System.err.println("ERROR: Can't query into the database");
    }

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
   * updateTradingCenter adds a new record into the trading center table.
   *
   * @param seller   userName of user making the listing
   * @param cropSell id of the crop that will be traded
   * @param sellQ    quantity of cropSell willing to be traded
   * @param cropBuy  id of crop that is requested in return
   * @param buyQ     quantity of cropBuy asked for
   */
  public static void updateTradingCenter(String seller, String cropSell, String sellQ,
      String cropBuy, String buyQ) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list.
      prep = conn.prepareStatement("INSERT INTO trading_center (trader, crop_sell, quant_sell, "
          + "crop_buy, quant_buy) VALUES (?,?,?,?,?);");
      prep.setString(1, seller);
      prep.setString(2, cropSell);
      prep.setString(3, sellQ);
      prep.setString(4, cropBuy);
      prep.setString(5, buyQ);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * getTradingCenter returns all the records in the trading center.
   *
   * @return ArrayList of listings represented as, String[5]
   */
  public static String getTradingCenter() {
    PreparedStatement prep;
    StringBuilder listings = new StringBuilder();
    ResultSet rs = null;
    try {
      prep = conn.prepareStatement("SELECT * FROM trading_center;");
      rs = prep.executeQuery();
      int rowIndex = 0;
      while (rs.next()) {
        String[] newListing = new String[5];
        listings.append(rs.getString(1)).append(",");
        listings.append(rs.getString(2)).append(",");
        listings.append(rs.getString(3)).append(",");
        listings.append(rs.getString(4)).append(",");
        listings.append(rs.getString(5)).append(";");
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
    }
    return listings.toString();
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
  public static void updateFriendsPending(String usernameToAddToPending,
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
      System.err.println("ERROR: Can't query into the database.");
    }
    // This will update the friend list of the other user.

  }

  /**
   * This method will return the pending friend list of a user a string that is
   * separated by commas for username that is in the string.
   *
   * @param username The user for which to get the pending list for.
   * @return the friends list returned as a string separated by commas.
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
  public static void updateFriendsPendingAfterAdding(String replacement, String user) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_data SET friendspending= ? WHERE username=?;");
      prep.setString(1, replacement);
      prep.setString(2, user);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
    }

  }

  /**
   * This method will initialize the user farm when they start the game.
   *
   * @param userName the user for who we are setting the farm to.
   */
  public static void initializeFarm(String userName) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_data SET farm= ? WHERE username=?;");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos;
      try {
        oos = new ObjectOutputStream(bos);

        FarmLand[][] thePlantation = new FarmLand[12][20];

        for (int i = 0; i < thePlantation.length; i++) {
          for (int j = 0; j < thePlantation[0].length; j++) {
            thePlantation[i][j] = new FarmLand();
          }
        }

        oos.writeObject(new FarmFile(thePlantation, userName, userName + "'s farm"));
        oos.flush();
        oos.close();
        bos.close();
      } catch (IOException e) {
        System.err.println("ERROR: Can't perform operation.");
      }
      byte[] data = bos.toByteArray();
      prep.setBytes(1, data);
      prep.setString(2, userName);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
    }

  }

  /**
   * This method will save the user farm whenever they do something or log out.
   *
   * @param userName the user for who we are setting the farm to.
   * @param farm     The farm object and in this case a TestFarm
   */
  public static void saveFarm(String userName, FarmFile farm) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement("UPDATE user_data SET farm= ? WHERE username=?;");
      byte[] data = DatabaseUtils.convertToByteArray(farm);

      prep.setBytes(1, data);
      prep.setString(2, userName);
      prep.executeUpdate();
      prep.close();
    } catch (IOException e) {
      System.err.println("ERROR: Something went wrong while serializing to byte array.");
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
    }

  }

  /**
   * This method will load the user farm for when the user logs in.
   *
   * @param userID the user for who we are loading the farm for
   * @return Will return the farm file instance.
   */
  public static FarmFile loadFarm(String userID) {
    PreparedStatement prep;
    ResultSet rs = null;
    FarmFile farmclass = null;
    try {
      prep = conn.prepareStatement("SELECT farm FROM user_data WHERE username=?;");
      prep.setString(1, userID);
      rs = prep.executeQuery();
      byte[] bytes = null;

      while (rs.next()) {
        bytes = rs.getBytes(1);
      }

      if (bytes == null) {
        return null;
      }

      farmclass = DatabaseUtils.convertByteArrayToObject(bytes, FarmFile.class);

      rs.close();
      prep.close();
    } catch (IOException e) {
      System.err.println("ERROR: Can't query into the database.");
      return null;
    } catch (ClassNotFoundException e) {
      System.err.println("ERROR: Can't read to this class.");
      return null;
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
      return null;
    }

    return farmclass;

  }

  /**
   * This method will retrieve one inventory item from the users inventory.
   *
   * @param userName the username for who to get the crop of.
   * @param category the type of inventory (database table) to look from
   * @param itemName the name of the crop.
   * @return it will return the number of the crop.
   */
  public static int getOneInventoryItem(String userName, String category, String itemName) {
    PreparedStatement prep;
    ResultSet rs = null;
    int ret = 0;
    try {
      prep = conn.prepareStatement(
          "SELECT " + itemName + " FROM user_inventory_" + category + " WHERE username= ?;");
      prep.setString(1, userName);

      rs = prep.executeQuery();
      while (rs.next()) {
        ret = rs.getInt(1);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR");
      return -1;
    }

    return ret;
  }

  /**
   * This method will update the inventory of the user.
   *
   * @param userName the username for whom to update inventory for
   * @param category the type of inventory (database table) to look from
   * @param itemName a string that represents the item name
   * @param number   the number to update the inventory of that item to.
   */
  public static void updateInventory(String userName, String category, String itemName,
      int number) {
    PreparedStatement prep;
    try {
      // update the string that represents the friend list pending.
      prep = conn.prepareStatement(
          "UPDATE user_inventory_" + category + " SET " + itemName + " = ? WHERE username=?;");
      prep.setInt(1, number);
      prep.setString(2, userName);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR");
    }

  }

  /**
   * This method will insert the map into the data base with the counter for which
   * map this is.
   *
   * @param id        the id of the map.
   * @param mapdata   the data of the farm represented as a string.
   * @param freeSpace The amount of free space that is in the map.
   */
  public static void insertMapIntoDataBase(int id, String mapdata, int freeSpace) {
    PreparedStatement prep;

    try {
      prep = conn.prepareStatement("INSERT INTO user_maps VALUES (?,?,?);");
      prep.setInt(1, id);
      prep.setString(2, mapdata);
      prep.setInt(3, freeSpace);
      prep.addBatch();
      prep.executeBatch();
      prep.close();
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
    }

  }

  /**
   * This method will retrieve the map from the database so that it can be loaded
   * into.
   *
   * @param id the id that represents the map id we will use to retrieve the map
   *           data.
   * @return reutrn the map data as a text to be sent to the front end.
   */
  public static String getMapFromDataBase(int id) {
    PreparedStatement prep;
    ResultSet rs = null;
    String mapdata = null;
    try {
      prep = conn.prepareStatement("SELECT mapdata FROM user_maps WHERE mapid = ?;");
      prep.setInt(1, id);
      rs = prep.executeQuery();
      while (rs.next()) {
        mapdata = rs.getString(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      System.err.println("ERROR: Can't query into the database.");
      return null;
    }
    return mapdata;
  }

  /**
   * This method will update the the free space that is available in a map.
   *
   * @param id        the id for the map in which we want to update the free space
   *                  of.
   * @param freeSpace the number to which we want to set the free space to.
   */
  public static void updateFreeSpaceInMaps(int id, int freeSpace) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_maps SET free_space=? WHERE mapid=?;");
      prep.setInt(1, freeSpace);
      prep.setInt(2, id);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR in update");
    }

  }

  /**
   * This method will return the amount of free spaces in the map given the id.
   *
   * @param id The id for which we will update the free space for.
   * @return the int representing the amount of free space left in the map.
   *         Returns -1 if errors.
   */
  public static int getFreeSpaceFromMap(int id) {
    PreparedStatement prep;
    ResultSet rs = null;
    int freeSpace = -1;
    try {
      prep = conn.prepareStatement("SELECT free_space FROM user_maps WHERE mapid = ?;");
      prep.setInt(1, id);
      rs = prep.executeQuery();
      while (rs.next()) {
        freeSpace = rs.getInt(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      return freeSpace;
    }
    return freeSpace;
  }

  /**
   * This method will retrieve the information of the most recent map that will be
   * used to check to see if we need to make a new map.
   *
   * @return It will return an array of Strings representing the info on the map.
   */
  public static String[] getDataFromMostRecentMap() {
    PreparedStatement prep;
    ResultSet rs = null;
    String[] mapInfo = null;
    try {
      prep = conn.prepareStatement("SELECT * FROM user_maps ORDER BY mapid DESC LIMIT 1;");
      rs = prep.executeQuery();
      while (rs.next()) {
        mapInfo = new String[3];
        mapInfo[0] = String.valueOf(rs.getInt(1));
        mapInfo[1] = rs.getString(2);
        mapInfo[2] = String.valueOf(rs.getInt(3));
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      System.err.println("ERROR in getting from recent");
      return mapInfo;
    }
    return mapInfo;
  }

  /**
   * This method will retrieve the map from the database so that it can be loaded
   * up for the user in the front end.
   *
   * @param id the user id of the user.
   * @return return the integer that represents the mad id of the user.
   */
  public static int getMapIDofUserFromDataBase(String id) {
    PreparedStatement prep;
    ResultSet rs = null;
    int mapid = -1;
    try {
      prep = conn.prepareStatement("SELECT mapid FROM user_data WHERE username = ?;");
      prep.setString(1, id);
      rs = prep.executeQuery();
      while (rs.next()) {
        mapid = rs.getInt(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      return -1;
    }
    return mapid;
  }

  /**
   * This method will update the map data from a click from the user whenever they
   * select their option.
   *
   * @param id      The id of the map to update.
   * @param mapdata The data to update the mapdata with.
   */
  public static void updateTheMapData(int id, String mapdata) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_maps SET mapdata=? WHERE mapid=?;");
      prep.setString(1, mapdata);
      prep.setInt(2, id);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR in update");
    }

  }

  /**
   * This method will update whether or not the user is a new user anymore so that
   * they can acess the home page and all the other functionlities of the game. If
   * they are still a new user they will be directed to the new user page.
   *
   * @param username the username for who to update the status for.
   * @param status   the status we are updating it to. true or false.
   */
  public static void updateNewUserIndication(String username, String status) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_data SET isNewUser=? WHERE username=?;");
      prep.setString(1, status);
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR in update");
    }

  }

  /**
   * This method will get the status of the user determining whether or not they
   * are a new user.
   *
   * @param username the user for who to get status of.
   * @return the status of the user represented as a string.
   */
  public static String getStatusOfUser(String username) {
    PreparedStatement prep;
    ResultSet rs = null;
    String status = null;
    try {
      prep = conn.prepareStatement("SELECT isNewUser FROM user_data WHERE username = ?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        status = rs.getString(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      return status;
    }
    return status;
  }

  /**
   * This method will set the row and the column of where the user clicks on the
   * map.
   *
   * @param username the username for who to update.
   * @param row      the row they picked.
   * @param col      the column they picked.
   */
  public static void updateTheRowAndColumnofUserLocationInMap(String username, int row, int col) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_data SET row=? WHERE username=?;");
      prep.setInt(1, row);
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
      prep = conn.prepareStatement("UPDATE user_data SET col=? WHERE username=?;");
      prep.setInt(1, col);
      prep.setString(2, username);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR in update");
    }

  }

  /**
   * This method will return the row and column of where the user clicked on the
   * map. it will be used to show the current user map so they know where they
   * chose their location.
   *
   * @param username the user for who to get the coords for.
   * @return the coordinates represented in an integer array.
   */
  public static int[] getRowAndColumnOfUserMapLocation(String username) {
    PreparedStatement prep;
    ResultSet rs = null;
    int[] coord = null;
    try {
      prep = conn.prepareStatement("SELECT row,col FROM user_data WHERE username = ?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      while (rs.next()) {
        coord = new int[2];
        coord[0] = rs.getInt(1);
        coord[1] = rs.getInt(2);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      return coord;
    }
    return coord;
  }

  /**
   * This method will get all the inventory items of a plater and store them in
   * map with the key being the string and the value being the interger.
   *
   * @param userName the user for who we are updating it for.
   * @return It will return a map that represents the inventory.
   */
  public static Map<String, Integer> getAllInventoryItems(String userName) {
    PreparedStatement prep;
    Map<String, Integer> ret = new HashMap<>();
    String[] cropNames = getAllCropNames();

    try {
      prep = conn.prepareStatement("SELECT * FROM user_inventory_crops WHERE username=?;");
      prep.setString(1, userName);

      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        for (int i = 0; i < cropNames.length; i++) {
          String key = cropNames[i];
          int value = rs.getInt(i + 2);
          if (value > 0) {
            ret.put(key, value);
          }
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR");
      return null;
    }

    return ret;
  }

  /**
   * This method will remove a trade from the list.
   *
   * @param tradeData It will take in the trade data that is represented as a
   *                  string.
   */
  public static void removeTradeListing(String tradeData) {
    String[] data = tradeData.split(",");
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("DELETE FROM trading_center WHERE trader = ? AND"
          + " crop_sell = ? AND quant_sell = ? AND crop_buy = ? AND quant_buy = ?;");
      prep.setString(1, data[0]);
      prep.setString(2, data[1]);
      prep.setString(3, data[2]);
      prep.setString(4, data[3]);
      prep.setString(5, data[4]);
      prep.executeUpdate();
      prep.close();
      System.out.println("Sql executed");
    } catch (SQLException e) {
      System.out.println("ERROR");
    }
  }

  /**
   * This method will get the username based on the row and column and map id.
   * This will be the friends username.
   *
   * @param row the row value.
   * @param col the column value.
   * @param id  the mapid value.
   * @return the username of the friend.
   */
  public static String getUserNameFromRowAndColumnOfUserMap(int row, int col, int id) {
    PreparedStatement prep;
    ResultSet rs = null;
    String user = null;
    try {
      prep = conn.prepareStatement(
          "SELECT username FROM user_data WHERE row = ? AND col = ? AND mapid = ?;");
      prep.setInt(1, row);
      prep.setInt(2, col);
      prep.setInt(3, id);
      rs = prep.executeQuery();
      while (rs.next()) {
        user = rs.getString(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      return user;
    }
    return user;
  }

  // --------------------------------------------------------------------------

  // ******************************
  // *user balance related queries*
  // ******************************
  /**
   * return the amount of cash a user has at the moment
   *
   * @param username user to look up
   * @return the amount of cash the input user has
   */
  public static int getUserBalance(String username) {
    PreparedStatement prep;
    ResultSet rs;
    int balance = 0;
    try {
      prep = conn.prepareStatement("SELECT balance FROM user_data WHERE username=?;");
      prep.setString(1, username);
      rs = prep.executeQuery();
      if (rs.next()) {
        balance = rs.getInt(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      System.out.println("SQL error while looking up balance for " + username);
    }
    return balance;
  }

  public static void updateUserBalance(String username, int newAmount) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_data SET balance=? WHERE username=?;");
      prep.setInt(1, newAmount);
      prep.setString(1, username);

      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("SQL error while updating balance for " + username);
    }
  }

  // --------------------------------------------------------------------------

  /*
   * returns the names of all the crops
   */
  public static String[] getAllCropNames() {
    String[] crops = {
        "tomatoes", "corn", "wheat", "cotton", "rice", "sugar", "apples", "pears", "oranges",
        "tangerines", "bananas", "strawberries", "kiwis", "watermelons", "avocados", "lettuce",
        "potatoes", "cucumbers", "carrots", "greenbeans", "cherries", "grapes", "lemons", "papayas",
        "peaches", "pineapples", "pomegranates", "cabbages", "kale", "peanuts", "pumpkins",
        "broccoli", "lavendar", "rosemary", "demo_crop", "demo_crop2"
    };
    return crops;
  }

  // ----------------------------------------------------------------------------

  // **********************
  // *crop related queries*
  // **********************
  private static void setCaches() {
    cropInfoCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE)
        .build(new CacheLoader<String, Object[]>() {
          @Override
          public Object[] load(String key) throws Exception {
            return getCropHelper(key);
          }
        });
  }

  public static Crop getCrop(String cropName, FarmLand land, int cropStatus) {
    Crop crop = new Crop(cropName);

    Object[] cropInfo;
    try {
      cropInfo = cropInfoCache.get(cropName);

      // record instant this crop is planted
      Instant now = Instant.now();

      // bind this crop to its land
      crop.setFarmLand(land);

      // 0: seeded or 2: mature (for multiharvest crops)
      crop.setCropStatus(cropStatus);

      // init this crop's ID
      crop.setID((int) cropInfo[0]);

      // init this crop's terrain set
      crop.setDesiredTerrains((Set<String>) cropInfo[1]);

      // init this crop's lifeCycleTimes
      Duration[] lifeCycleTimes = (Duration[]) cropInfo[2];
      crop.setLifeCycleTimes(lifeCycleTimes);

      // default wither duration for each stage except harvest
      crop.setWitherDuration((Duration) cropInfo[3]);

      // init min yield
      crop.setMinYield((int) cropInfo[4]);

      // init max yield
      crop.setMaxYield((int) cropInfo[5]);

      // init yield
      int yield = (int) (Math.random() * ((int) cropInfo[5] - (int) cropInfo[4] + 1))
          + (int) cropInfo[4];
      crop.setYield(yield);

      // init stealable yield
      crop.setStealableYield((int) (yield * 0.5));

      // init map holding people who have stolen this crop
      crop.setRecordedThief(new HashMap<String, Integer>());

      // init max harvest times
      crop.setMaxHarvestTimes((int) cropInfo[6]);

      // default currentHarvestTimes to max
      crop.setCurrentHarvestTimes((int) cropInfo[6]);

      // init probabilities this crop gets infested
      crop.setSproutInfestChance((int) cropInfo[7]);
      crop.setMatureInfestChance((int) cropInfo[8]);

      // determine whether this crop will be infested
      boolean isMatureInfested = (int) (Math.random() * 100) + 1 <= (int) cropInfo[8];
      boolean isSproutInfested = (int) (Math.random() * 100) + 1 <= (int) cropInfo[7];
      crop.setIsSproutInfested(isSproutInfested);
      crop.setIsMatureInfested(isMatureInfested);

      // init life cycle time for first stage (0 or 2)
      crop.setDurationUntilNextStage(lifeCycleTimes[cropStatus]);

      // init auto wither time from current stage
      crop.setWitheredInstant(now.plus((Duration) cropInfo[3]));

      // init sprout and mature infest duration
      crop.setDurationUntilSproutInfested(Duration.ZERO);
      crop.setDurationUntilMatureInfested(Duration.ZERO);

      if (isSproutInfested) {
        int percentage = (int) (Math.random() * 81) + 10;
        Duration durationUntilInfested = lifeCycleTimes[1].multipliedBy(percentage).dividedBy(100);
        crop.setDurationUntilSproutInfested(durationUntilInfested);
        crop.setMatureInfestedInstant(now.plus(durationUntilInfested));
      }

      if (isMatureInfested) {
        System.out.println("mature infested");
        int percentage = (int) (Math.random() * 81) + 10;
        Duration durationUntilInfested = lifeCycleTimes[2].multipliedBy(percentage).dividedBy(100);
        crop.setDurationUntilMatureInfested(durationUntilInfested);
      }

      // init sprout and mature infest instant
      crop.setSproutInfestedInstant(Instant.MAX);
      crop.setMatureInfestedInstant(Instant.MAX);

      // init isInfested
      // NOTE: set this false before respawn condition
      crop.setIsInfested(false);

      // if respawn: initialize infest instant
      if (cropStatus == 2 && isMatureInfested) {
        crop.setMatureInfestedInstant(now.plus(crop.getDurationUntilMatureInfested()));
        crop.setIsInfested(true);
      }

      // init time next stage based on water status
      if (land.isWatered(now)) {
        Instant nextDryInstant = land.getNextDryInstant();

        // watered, start timer
        crop.startGrowing(now);

        // pause growing at appropriate time (if necessary)
        if (cropStatus == 1 && crop.getSproutInfestedInstant().isBefore(nextDryInstant)) {
          // infested before land dries
          crop.pauseGrowing(crop.getSproutInfestedInstant());
          crop.setIsInfested(true);
        } else if (cropStatus == 2 && crop.getMatureInfestedInstant().isBefore(nextDryInstant)) {
          crop.pauseGrowing(crop.getMatureInfestedInstant());
          crop.setIsInfested(true);
        } else if (!(crop.getNextStageInstant().isBefore(nextDryInstant))) {
          // pause growth if nextStageInstant is on or after next time to dry
          crop.pauseGrowing(nextDryInstant);
        }
      } else {
        // not watered, start growing AS SOON AS it's watered
        crop.stopGrowing();
      }

      // ----------------------------------------------------

      return crop;
    } catch (ExecutionException e) {
      System.out.println("Failed to load crop info from cache");
      return null;
    }
  }

  private static Object[] getCropHelper(String cropName) {
    PreparedStatement prep;

    // index:
    // 0: int id
    // 1: Set<String> desired terrains
    // 2: Duration[] lifeCycleTimes
    // 3: Duration witherDuration
    // 4: int minYield
    // 5: int maxYield
    // 6: int maxHarvestTimes
    // 7: int sproutInfestRate
    // 8: int matureInfestRate
    Object[] cropInfo = new Object[9];

    try {
      prep = conn.prepareStatement("SELECT * FROM crop_data_growth WHERE name=?;");
      prep.setString(1, cropName);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        // id
        cropInfo[0] = rs.getInt(2);

        // desired terrains (separated by #)
        HashSet<String> terrainSet = new HashSet<String>();
        for (String s : rs.getString(3).split("#")) {
          terrainSet.add(s);
        }
        cropInfo[1] = terrainSet;

        // lifeCycleTimes
        Duration[] lifeCycleTimes = new Duration[5];
        String[] times = rs.getString(4).split("#");
        for (int i = 0; i < 5; i++) {
          // TODO: change crop growth time scale here if necessary
          lifeCycleTimes[i] = Duration.ofSeconds(Long.parseLong(times[i]));
        }
        cropInfo[2] = lifeCycleTimes;

        // witherDuration
        cropInfo[3] = Duration.ofSeconds(rs.getInt(5));

        // minYield
        cropInfo[4] = rs.getInt(6);

        // maxYield
        cropInfo[5] = rs.getInt(7);

        // maxHarvestTimes
        cropInfo[6] = rs.getInt(8);

        // sproutInfestRate
        cropInfo[7] = rs.getInt(9);

        // matureInfestRate
        cropInfo[8] = rs.getInt(10);
      }
      prep.close();
      rs.close();

      return cropInfo;
    } catch (SQLException e) {
      System.out.println("Database error encountered while trying to query crop data");
      return null;
    }

  }

  // ----------------------------------------------------------------------------

  // ***********************
  // *price related queries*
  // ***********************

  /**
   * return an item's price (buy or sell)
   *
   * @param category  category of item
   * @param buyOrSell literal string "buy" or "sell"
   * @param itemName  name of the item to look up
   * @return the requested item's buying or selling price, or -1 if error
   *         encountered
   */
  public static int getOneItemPrice(String category, String buyOrSell, String itemName) {
    PreparedStatement prep;
    ResultSet rs;
    int price = -1;;

    try {
      prep = conn.prepareStatement(
          "SELECT " + buyOrSell + " FROM price_data_" + category + " WHERE name=?");
      prep.setString(1, itemName);
      rs = prep.executeQuery();
      if (rs.next()) {
        price = rs.getInt(1);
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      System.out.println("SQL error encountered while looking for buy price");
    }
    return price;
  }

  // ----------------------------------------------------------------------------

  // **********************
  // *user related queries*
  // **********************

  public static List<String> getAllUserNameWithFarm() {
    PreparedStatement prep;
    ResultSet rs;
    List<String> names = new LinkedList<String>();

    try {
      prep = conn.prepareStatement("SELECT username FROM user_data WHERE isNewUser == ?");
      prep.setString(1, "false");
      rs = prep.executeQuery();

      while (rs.next()) {
        names.add(rs.getString(1));
      }
      prep.close();
      rs.close();
    } catch (SQLException e) {
      System.out.println("SQL error encountered while getting all usernames with farm");
    }

    return names;
  }

  // ----------------------------------------------------------------------------

  // **********************
  // *Tool related queries*
  // **********************

  /**
   * Return the size of input user's inventory
   *
   * @param username user's name
   * @return the size of input user's inventory
   */
  public static int getInventorySizeByUsername(String username) {
    PreparedStatement prep;
    ResultSet rs;

    // TODO: create database tables for this and remove the mock below

//    try {
//      prep = conn.prepareStatement("SELECT * FROM ____ WHERE username == ?");
//      prep.setString(1, username);
//      rs = prep.executeQuery();
//
//      while (rs.next()) {
//      }
//
//      prep.close();
//      rs.close();
//    } catch (SQLException e) {
//      System.out.println("SQL error while querying for user's inventory dimensions");
//    }

    // mock data for now

    return 8 * 5;
  }

  /**
   * Return all tools owned by the input username and the amount of each tool
   * (will NOT return items that have a count of 0)
   *
   * @param username user's name
   * @return a map mapping each item's name to an array of item's type, and amount
   *         (0: type, 1: amount)
   */
  public static Map<String, String[]> getAllToolsByUsername(String username) {
    PreparedStatement prep;
    ResultSet rs;
    String[] toolTables = {
        "user_inventory_tools_cure", "user_inventory_tools_harvest", "user_inventory_tools_plow",
        "user_inventory_tools_seeds", "user_inventory_tools_steal", "user_inventory_tools_water"
    };
    Map<String, String[]> toolMap = new HashMap<String, String[]>();

    try {
      for (String tableName : toolTables) {
        prep = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE username == ?");
        prep.setString(1, username);
        rs = prep.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
          int columnCount = rsmd.getColumnCount();

          for (int i = 1; i <= columnCount; i++) {
            int itemCount = rs.getInt(i);
            if (itemCount > 0) {
              String[] info = {
                  tableName.substring(21),
                  // use substring here to find the type name
                  String.valueOf(itemCount)
              };

              toolMap.put(rsmd.getColumnName(i), info);
            }
          }
        }

        prep.close();
        rs.close();
      }
    } catch (SQLException e) {
      System.out.println("SQL error encountered while getting " + username + "'s tools");
      e.printStackTrace();
    }

    return toolMap;
  }

  public static String[][] getShortcutToolsByUsername(String username) {
    PreparedStatement prep;
    ResultSet rs;
    // array format: [type][name]
    String[][] tools = null;

    try {
      prep = conn.prepareStatement("SELECT shortcutTools FROM user_data WHERE username == ?");
      prep.setString(1, username);
      rs = prep.executeQuery();
      byte[] bytes = null;

      while (rs.next()) {
        bytes = rs.getBytes(1);
      }

      if (bytes == null) {
        return tools;
      }

      tools = DatabaseUtils.convertByteArrayToObject(bytes, String[][].class);

      prep.close();
      rs.close();
    } catch (IOException e) {
      System.out.println("IOException occurred while getting shortcut tools");
    } catch (ClassNotFoundException e) {
      System.out.println("Failed to deserialize while getting shortcut tools");
    } catch (SQLException e) {
      System.out.println("SQL error encountered while getting shortcut tools");
    }

    return tools;
  }

  public static void setShortcutToolsByUsername(String username, int slotNumber, String toolType,
      String toolName) {
    String[][] oldShortcuts = getShortcutToolsByUsername(username);
    oldShortcuts[slotNumber][0] = toolType;
    oldShortcuts[slotNumber][1] = toolName;

    PreparedStatement prep;
    try {
      prep = conn.prepareStatement("UPDATE user_data SET shortcutTools= ? WHERE username=?;");
      prep.setBytes(1, DatabaseUtils.convertToByteArray(oldShortcuts));
      prep.setString(2, username);

      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  // ----------------------------------------------------------------------------------
}
