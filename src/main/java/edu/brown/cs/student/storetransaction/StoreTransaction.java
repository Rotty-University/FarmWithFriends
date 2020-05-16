package edu.brown.cs.student.storetransaction;

import edu.brown.cs.student.proxy.FarmProxy;

public final class StoreTransaction {
  private StoreTransaction() {
    // hide constructor for final classes
  }

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

    FarmProxy.updateUserBalance(username, FarmProxy.getUserBalance(username) - totalPrice);
    FarmProxy.updateInventory(username, category, itemName,
        FarmProxy.getOneInventoryItem(username, category, itemName) + amount);

    return totalPrice;
  }
} // end of class
