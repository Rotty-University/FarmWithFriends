package edu.brown.cs.student.farm;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import edu.brown.cs.student.proxy.FarmProxy;

public class FarmViewer {

  // current farm's data
  private String ownerName;
  private FarmFile serializedFarm;
  private FarmLand[][] thePlantation;
  private String farmName;

  public FarmViewer(String ownerName) {
    // init
    FarmFile farmFile = FarmProxy.loadFarm(ownerName);
    if (farmFile == null) {
      // farm's user id NOT valid, fail
      System.out.println("Failed to load farm: it does not exist");
      return;
    }

    // farm is valid, proceed
    this.ownerName = ownerName;
    serializedFarm = farmFile;
    thePlantation = farmFile.getThePlantation();
    farmName = farmFile.getFarmName();
  }

  // Helper methods ------------------------------------------------------------

  // save current state of farm
  public void saveFarm() {
    if (thePlantation == null || serializedFarm == null) {
      return;
    }

    // update farm file references
    serializedFarm.setThePlantation(thePlantation);
    FarmProxy.saveFarm(ownerName, serializedFarm);
  }

  // update and print the current layout of the farm
  public void showFarm() {
    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    Instant now = Instant.now();
    System.out.println("Farm shown at: " + now);

    for (FarmLand[] l : thePlantation) {
      for (FarmLand j : l) {
        // update land first
        j.updateWaterStatus(now);

        if (j.isOccupied()) {
          // there is a crop on this land
          Crop c = j.getCrop();

          // update crop if necessary
          c.updateStatus(now);

          System.out.print("[Crop ID: " + c.getID() + ", watered: " + j.isWatered(now)
              + ", crop status: " + c.getCropStatus() + "] ");

        } else if (j.isWatered(now)) {
          System.out.print("[ WATEREDLAND ]");
        } else if (j.isPlowed()) {
          System.out.print("[ PLOWED ]");
        } else {
          System.out.print("[ FARMLAND ]");
        }
      }

      System.out.println();
    }
  }

  // update ONE specific tile
  public void updateFarm() {
    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    Instant now = Instant.now();

    for (FarmLand[] l : thePlantation) {
      for (FarmLand j : l) {
        // update land first
        j.updateWaterStatus(now);

        if (j.isOccupied()) {
          // there is a crop on this land
          // update crop if necessary
          j.getCrop().updateStatus(now);
        }
      }
    }

    saveFarm();
  }

  // ---------------------------------------------------------------------------

  // ********************
  // *Operation Commands*
  // ********************

  /**
   * Perform "plow" operation on the given plot of land
   *
   * @param username user requesting this operation
   * @param row      row of the given plot of land
   * @param col      col of the given plot of land
   */
  public void plow(String username, int row, int col) {

    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    if (!username.equals(ownerName)) {
      System.out.println("Can't do that: you're not the owner");

      return;
    }

    FarmLand l = thePlantation[row][col];

    if (l.isOccupied() && l.getCrop().getCropStatus() != 5) {
      System.out.println("Don't plow the plant, you worked hard on it");
      return;
    }

    l.setCrop(null);
    l.setIsPlowed(true);

    // save after every command
    saveFarm();
  }

  /**
   * Perform "plant" operation on the given plot of land
   *
   * @param username user requesting this operation
   * @param cropName name of the crop to be planted
   * @param row      row of the given plot of land
   * @param col      col of the given plot of land
   */
  public void plant(String username, String cropName, int row, int col) {

    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    if (!username.equals(ownerName)) {
      System.out.println("Can't do that: you're not the owner");

      return;
    }

    FarmLand l = thePlantation[row][col];

    if (!l.isPlowed()) {
      System.out.println("Please plow this land first");
      return;
    }

    if (l.isOccupied()) {
      System.out.println("This land is already occupied");
      return;
    }

    try {
      // legacy code: create crop class using reflection
//        Class<?> clazz = Class.forName("edu.brown.cs.student.crops." + cropName);
//        Constructor<?> constructor = clazz.getConstructor(FarmLand.class, int.class);
//        ACrop newCrop = (ACrop) constructor.newInstance(l, 0);

      Crop newCrop = FarmProxy.getCrop(cropName, l, 0);
      l.setCrop(newCrop);
    } catch (Exception e) {
      System.out.println("Something wong");
      e.printStackTrace();
    }

    // save after every command
    saveFarm();
  }

  /**
   * Perform "water" operation on the given plot of land
   *
   * @param row               row of the given plot of land
   * @param col               col of the given plot of land
   * @param durationInSeconds how long the water effect should last
   */
  public void water(int row, int col, int durationInSeconds) {

    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    Instant now = Instant.now();

    FarmLand l = thePlantation[row][col];

    if (!l.isPlowed()) {
      System.out.println("Plow first then water");
      return;
    }

    // water the land
    l.water(now, Duration.ofSeconds(durationInSeconds));

    // save after every command
    saveFarm();
  }

  /**
   * Perform "harvest" operation on the given plot of land
   *
   * @param username user requesting this operation
   * @param row      row of the given plot of land
   * @param col      col of the given plot of land
   */
  public void harvest(String username, int row, int col) {

    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    if (!username.equals(ownerName)) {
      System.out.println("Can't do that: you're not the owner");

      return;
    }

    FarmLand l = thePlantation[row][col];
    Crop c = l.getCrop();
    Instant now = Instant.now();

    if (!l.isOccupied()) {
      System.out.println("Can't harvest here, your didn't plant anything");
      return;
    }

    c.updateStatus(now);

    if (c.getCropStatus() == 3 || c.getCropStatus() == 4) {
      // can harvest
      String cropName = c.getName();
      int yield = c.getYield();

      // update inventory
      int oldVal = FarmProxy.getOneInventoryItem(username, "crops", cropName);
      int total = oldVal + yield;
      FarmProxy.updateInventory(username, "crops", cropName, total);

      // update crop/land status
      l.setCrop(l.getCrop().respawn());

      System.out.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
    } else if (c.getCropStatus() == 5) {
      // crop withered
      System.out.println("You have left your plant neglected for too long, plow and star over");
    } else {
      // cannot harvest yet
      System.out.println("Crop cannot be harvested yet, you should work harder");
    }

//      showFarm();

    // save after every command
    saveFarm();
  }

  public void inspect(String username) {

    System.out.println("You have: ");

    Map<String, Integer> inventory = FarmProxy.getAllInventoryItems(username);

    for (String s : inventory.keySet()) {
      System.out.println(inventory.get(s) + " units of " + s);
    }
  }

  public void steal(String username, int row, int col) {

    if (thePlantation == null) {
      System.out.println("Can't do that: no farm selected");

      return;
    }

    if (username.equals(ownerName)) {
      System.out.println("Can't do that: you ARE the owner");

      return;
    }

    FarmLand l = thePlantation[row][col];
    Crop c = l.getCrop();
    Instant now = Instant.now();

    if (!l.isOccupied()) {
      System.out.println("Nothing to steal here");
      return;
    }

    c.updateStatus(now);

    if (c.getCropStatus() == 4) {
      // can steal
      String cropName = c.getName();
      int yield = c.getYield();

      // update inventory
      int oldVal = FarmProxy.getOneInventoryItem(username, "crops", cropName);
      int total = oldVal + yield;
      FarmProxy.updateInventory(username, "crops", cropName, total);

      // update crop/land status
      l.setCrop(l.getCrop().respawn());

      System.out.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
    } else if (c.getCropStatus() == 5) {
      // crop withered
      System.out.println("You have left your plant neglected for too long, plow and star over");
    } else {
      // cannot harvest yet
      System.out.println("Crop cannot be harvested yet, you should work harder");
    }

    // save after every command
    saveFarm();
  }

  // ---------------------------------------------------------------------------------

  // mutators
  public FarmLand[][] getThePlantation() {
    return thePlantation;
  }

  public void setThePlantation(FarmLand[][] f) {
    thePlantation = f;
  }

  /**
   * @return the owner name
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * @return the farmName
   */
  public String getFarmName() {
    return farmName;
  }

  // ---------------------------------------------------------------------------------

} // end of class
