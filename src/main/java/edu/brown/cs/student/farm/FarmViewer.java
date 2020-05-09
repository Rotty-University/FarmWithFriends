package edu.brown.cs.student.farm;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import edu.brown.cs.student.crops.ACrop;
import edu.brown.cs.student.proxy.FarmProxy;
import edu.brown.cs.student.repl.Command;
import edu.brown.cs.student.repl.REPL;

public class FarmViewer {
  // user who is viewing this farm
  private String viewerName;

  // current farm's data
  private String ownerName;
  private FarmFile serializedFarm;
  private FarmLand[][] thePlantation;
  private String farmName;

  public FarmViewer(REPL repl, String viewerName) {
    // init
    this.viewerName = viewerName;
    // default to no farm loaded at init
    ownerName = null;
    farmName = null;
    serializedFarm = null;
    thePlantation = null;

    repl.register("plant", new PlantCommand());
    repl.register("plow", new PlowCommand());
    repl.register("s", new ShowCommand());
    repl.register("water", new WaterCommand());
    repl.register("harvest", new HarvestCommand());
    repl.register("inspect", new InspectInventoryCommand());
    repl.register("switch", new SwitchCommand());
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
          ACrop c = j.getCrop();

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
  public void updateOneTile(int row, int col) {
    if (thePlantation == null) {
      System.out.println("Can't update this tile: no farm selected");

      return;
    }

    Instant now = Instant.now();
    FarmLand l = thePlantation[row][col];

    l.updateWaterStatus(now);

    if (l.isOccupied()) {
      // there is a crop on this land
      // update crop if necessary
      l.getCrop().updateStatus(now);
    }
  }

  // ---------------------------------------------------------------------------

  // command to set current farm to another farm
  public class SwitchCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      String nextOwnerName = tokens[0];

      FarmFile nextFarmFile = FarmProxy.loadFarm(nextOwnerName);
      if (nextFarmFile == null) {
        // new farm's user id NOT valid, fail
        pw.println("Failed to load farm: it does not exist");
        return;
      }

      // new farm is valid, proceed
      // first save current farm
      saveFarm();

      ownerName = nextOwnerName;
      serializedFarm = nextFarmFile;
      thePlantation = nextFarmFile.getThePlantation();
      farmName = nextFarmFile.getFarmName();

      System.out.println("Welcome to " + ownerName + "'s farm");
    }

  } // end of switch command class

  // command to print the farm
  public class ShowCommand implements Command {
    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      showFarm();

      // save after every command
      saveFarm();
    }
  }

  // plow a land
  public class PlowCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!viewerName.equals(ownerName)) {
        pw.println("Can't do that: you're not the owner");

        return;
      }

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];

      if (l.isOccupied() && l.getCrop().getCropStatus() != 5) {
        pw.println("Don't plow the plant, you worked hard on it");
        return;
      }

      l.setCrop(null);
      l.setIsPlowed(true);

//      showFarm();

      // save after every command
      saveFarm();
    }
  }

  // plant a seed
  public class PlantCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!viewerName.equals(ownerName)) {
        pw.println("Can't do that: you're not the owner");

        return;
      }

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      String cropName = tokens[2];
      FarmLand l = thePlantation[x][y];

      if (!l.isPlowed()) {
        pw.println("Please plow this land first");
        return;
      }

      if (l.isOccupied()) {
        pw.println("This land is already occupied");
        return;
      }

      try {
        Class<?> clazz = Class.forName("edu.brown.cs.student.crops." + cropName);
        Constructor<?> constructor = clazz.getConstructor(FarmLand.class, int.class);
        ACrop newCrop = (ACrop) constructor.newInstance(l, 0);

        l.setCrop(newCrop);
      } catch (Exception e) {
        System.out.println("Something wong");
        e.printStackTrace();
      }

//      showFarm();

      // save after every command
      saveFarm();
    }
  }

  public class WaterCommand implements Command {
    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

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

//      showFarm();

      // save after every command
      saveFarm();
    }
  }

  public class HarvestCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!viewerName.equals(ownerName)) {
        pw.println("Can't do that: you're not the owner");

        return;
      }

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];
      ACrop c = l.getCrop();
      Instant now = Instant.now();

      if (!l.isOccupied()) {
        pw.println("Can't harvest here, your didn't plant anything");
        return;
      }

      c.updateStatus(now);

      if (c.getCropStatus() == 3 || c.getCropStatus() == 4) {
        // can harvest
        String cropName = c.getName();
        int yield = c.getYield();

        // update inventory
        // TODO: add getInventory method to proxy
        int oldVal = FarmProxy.getOneInventoryItem(viewerName, cropName);
        int total = oldVal + yield;
        FarmProxy.updateInventory(viewerName, cropName, total);

        // update crop/land status
        l.setCrop(l.getCrop().respawn());

        pw.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
      } else if (c.getCropStatus() == 5) {
        // crop withered
        pw.println("You have left your plant neglected for too long, plow and star over");
      } else {
        // cannot harvest yet
        pw.println("Crop cannot be harvested yet, you should work harder");
      }

//      showFarm();

      // save after every command
      saveFarm();
    }
  } // end of harvest command class

  public class InspectInventoryCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      pw.println("You have: ");

      // TODO: add getter to proxy
      Map<String, Integer> inventory = FarmProxy.getAllInventoryItems(viewerName);

      for (String s : inventory.keySet()) {
        pw.println(inventory.get(s) + " units of " + s);
      }
    }

  }

  // mutators ------------------------------------------------------------
  public FarmLand[][] getThePlantation() {
    return thePlantation;
  }

  public void setThePlantation(FarmLand[][] f) {
    thePlantation = f;
  }

  /**
   * @return the viewer name
   */
  public String getViewerName() {
    return viewerName;
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
