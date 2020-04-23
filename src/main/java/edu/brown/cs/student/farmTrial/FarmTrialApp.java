package edu.brown.cs.student.farmTrial;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.student.crops.Crop;
import edu.brown.cs.student.crops.Tomato;
import edu.brown.cs.student.repl.Command;
import edu.brown.cs.student.repl.REPL;

public class FarmTrialApp {
  // welcome to my farm
  private TestFarm serializedFarm;
  private FarmLand[][] thePlantation;

  // user data
  private Map<Integer, Integer> inventory;
  private String farmName;

  public FarmTrialApp(REPL repl) {
    // init
    initializeFarm();

    repl.register("plant", new PlantCommand());
    repl.register("plow", new PlowCommand());
    repl.register("s", new ShowCommand());
    repl.register("water", new WaterCommand());
    repl.register("harvest", new HarvestCommand());
    repl.register("inspect", new InspectInventoryCommand());
  }

  // Helper methods ------------------------------------------------------------
  // initialize the farm
  void initializeFarm() {
    System.out.println("Welcome to my farm");

    farmName = "myFarm";

    try {
      // Reading the object from a file
      FileInputStream file = new FileInputStream(farmName + ".ser");
      ObjectInputStream in = new ObjectInputStream(file);

      // Method for deserialization of object
      serializedFarm = (TestFarm) in.readObject();

      // retrieve values from deserialized object
      thePlantation = serializedFarm.getThePlantation();
      inventory = serializedFarm.getInventory();

      in.close();
      file.close();

      System.out.println("Loaded \"" + farmName + "\"");
    } catch (Exception e) {
      // no file saved, create new plantation
      thePlantation = new FarmLand[1][4];
      inventory = new HashMap<Integer, Integer>();

      for (int i = 0; i < thePlantation.length; i++) {
        for (int j = 0; j < thePlantation[0].length; j++) {
          thePlantation[i][j] = new FarmLand();
        }
      }

      // init new farm to save
      serializedFarm = new TestFarm(thePlantation, inventory, farmName);

      System.out.println("No save file found, creating a new farm");
    } // end of catch

    // new farm or old farm, always show farm
    showFarm();
  }

  // save current state of farm
  boolean saveFarm() {
    // Serialization
    try {
      // Saving of object in a file
      FileOutputStream file = new FileOutputStream(farmName + ".ser");
      ObjectOutputStream out = new ObjectOutputStream(file);

      // Method for serialization of object
      out.writeObject(serializedFarm);

      out.close();
      file.close();

      System.out.println("Your plantation has been saved successfully");
      return true;

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to save plantation, IOException caught");
      return false;
    }
  }

  // print the current layout of the farm
  void showFarm() {
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

  // ---------------------------------------------------------------------------

  // command to print the farm
  class ShowCommand implements Command {
    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      showFarm();

      // save after every command
      saveFarm();
    }
  }

  // plow a land
  class PlowCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];

      if (l.isOccupied() && l.getCrop().getCropStatus() != 5) {
        pw.println("Don't plow the plant, you worked hard on it");
        return;
      }

      l.setCrop(null);
      l.setIsPlowed(true);

      showFarm();

      // save after every command
      saveFarm();
    }
  }

  // plant a seed
  class PlantCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];

      if (!l.isPlowed()) {
        pw.println("Please plow this land first");
        return;
      }

      if (l.isOccupied()) {
        pw.println("This land is already occupied");
        return;
      }

      l.setCrop(new Tomato(thePlantation[x][y]));

      showFarm();

      // save after every command
      saveFarm();
    }
  }

  class WaterCommand implements Command {
    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      Instant now = Instant.now();

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];

      if (!l.isPlowed()) {
        pw.println("Plow first then water");
        return;
      }

      // water the land
      l.water(now, Duration.ofSeconds(10));

      showFarm();

      // save after every command
      saveFarm();
    }
  }

  class HarvestCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];
      Crop c = l.getCrop();

      if (!l.isOccupied()) {
        pw.println("Can't harvest here, your didn't plant anything");
        return;
      }

      if (c.getCropStatus() == 3 || c.getCropStatus() == 4) {
        // can harvest
        int id = c.getID();
        int yield = c.getYield();

        // update inventory
        inventory.put(id, inventory.getOrDefault(id, 0) + yield);

        // update land status
        // TODO: if crop can harvest multiple times
        l.setCrop(null);

        pw.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
      } else if (c.getCropStatus() == 5) {
        // crop withered
        pw.println("You have left your plant neglected for too long, plow and star over");
      } else {
        // cannot harvest yet
        pw.println("Crop cannot be harvested yet, you should work harder");
      }

      showFarm();

      // save after every command
      saveFarm();
    }
  } // end of harvest command class

  class InspectInventoryCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      pw.println("You have: ");

      for (int i : inventory.keySet()) {
        pw.println(inventory.get(i) + " units of crop (ID: " + i + ")");
      }
    }

  }

  // mutators ------------------------------------------------------------
  public FarmLand[][] getThePlantation() {
    return thePlantation;
  }

  /**
   * @return the inventory
   */
  public Map<Integer, Integer> getInventory() {
    return inventory;
  }

  /**
   * @param inventory the inventory to set
   */
  public void setInventory(Map<Integer, Integer> inventory) {
    this.inventory = inventory;
  }

  // ---------------------------------------------------------------------------------

} // end of class
