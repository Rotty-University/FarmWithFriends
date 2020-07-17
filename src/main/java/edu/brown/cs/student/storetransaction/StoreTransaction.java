package edu.brown.cs.student.storetransaction;

import edu.brown.cs.student.proxy.FarmProxy;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public final class StoreTransaction extends TimerTask {
  private StoreTransaction() {
    // hide constructor for final classes
  }

  /**
   * Queries the database to randomly select 8 items for the store to
   * sell to a player based off what items they have unlocked.
   */
  @Override
  public void run() {
    try {
      FarmProxy.generateStoreItems();
    } catch (SQLException e) {
      System.out.print("SQL error: could not generate store items");
    }
  }

  /**
   * Updates the items offered in the store each day.
   */
  public static void stockStore() {
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    // every day at 12am the items in the store are updated
    Timer timer = new Timer();

    timer.schedule();
  }

  /**
   * Queries the database to randomly select 8 items for the store to
   * sell to a player based off what items they have unlocked.
   */
//  private void update() {
//    try {
//      FarmProxy.generateStoreItems();
//    } catch (SQLException e) {
//     System.out.print("SQL error: could not generate store items");
//    }
//  }

  /**
   * attempt to sell the input item to the store
   *
   * @param username user attempting to sell
   * @param category the category of item to sell
   * @param itemName the item to sell
   * @param amount   the amount of item to sell
   * @return total price of the sale if transaction was successful, a negative
   *         integer otherwise
   */
  public static int sellToStore(String username, String category, String itemName, int amount) {
    // -1: insufficient inventory
    // -2: store is no longer in need of this item
    // -3: database problem

    // TODO: make sure to handle negative or decimal amount on frontend
    int currentInventory = FarmProxy.getOneInventoryItem(username, category, itemName);
    if (currentInventory < amount) {
      return -1;
    }

    int profit = FarmProxy.getOneItemPrice(category, "sell", itemName) * amount;
    FarmProxy.updateInventory(username, category, itemName, currentInventory - amount);
    FarmProxy.updateUserBalance(username, FarmProxy.getUserBalance(username) + profit);

    return profit;
  }

  /**
   * attempt to buy the input item from the store
   *
   * @param username user attempting to buy
   * @param category the category of item to buy
   * @param itemName the item to buy
   * @param amount   the amount of item to buy
   * @return total price of the purchase if transaction was successful, a negative
   *         integer otherwise
   */
  public static int buyFromStore(String username, String category, String itemName, int amount) {
    // -1: not enough money to buy
    // -2: this item is no longer in stock
    // -3: database problem

    // TODO: make sure to handle negative or decimal amount on frontend
    int totalPrice = FarmProxy.getOneItemPrice(category, "buy", itemName) * amount;
    int currentBalance = FarmProxy.getUserBalance(username);
    if (currentBalance < totalPrice) {
      return -1;
    }
    int storeAmt = FarmProxy.getStoreItemAmt(itemName, category);
    // database problem
    if (storeAmt == -1) {
      return -3;
    }
    // check if item is in stock
    if (storeAmt < amount) {
      return -2;
    }
    FarmProxy.updateStoreBalance(storeAmt - amount, itemName, category);
    FarmProxy.updateUserBalance(username, FarmProxy.getUserBalance(username) - totalPrice);
    FarmProxy.updateInventory(username, category, itemName,
        FarmProxy.getOneInventoryItem(username, category, itemName) + amount);

    return totalPrice;
  }

} // end of class
