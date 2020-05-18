package edu.brown.cs.student.farm;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import edu.brown.cs.student.proxy.FarmProxy;
import edu.brown.cs.student.repl.Command;
import edu.brown.cs.student.repl.REPL;

public class FarmViewer {

  // current farm's data
  private String ownerName;
  private FarmFile serializedFarm;
  private FarmLand[][] thePlantation;
  private String farmName;
  private Command plantCommand, plowCommand, showCommand, waterCommand, harvestCommand,
      inspectCommand;

  public FarmViewer(REPL repl, String ownerName) {
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

    // set commands
    plantCommand = new PlantCommand();
    plowCommand = new PlowCommand();
    showCommand = new ShowCommand();
    waterCommand = new WaterCommand();
    harvestCommand = new HarvestCommand();
    inspectCommand = new InspectInventoryCommand();

    repl.register("plant", plantCommand);
    repl.register("plow", plowCommand);
    repl.register("s", showCommand);
    repl.register("water", waterCommand);
    repl.register("harvest", harvestCommand);
    repl.register("inspect", inspectCommand);
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
      String username = tokens[3];

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!username.equals(ownerName)) {
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
      String username = tokens[3];

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!username.equals(ownerName)) {
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
      String username = tokens[3];

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (!username.equals(ownerName)) {
        pw.println("Can't do that: you're not the owner");

        return;
      }

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];
      Crop c = l.getCrop();
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
        int oldVal = FarmProxy.getOneInventoryItem(username, "crops", cropName);
        int total = oldVal + yield;
        FarmProxy.updateInventory(username, "crops", cropName, total);

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
      String username = tokens[3];

      pw.println("You have: ");

      // TODO: add getter to proxy
      Map<String, Integer> inventory = FarmProxy.getAllInventoryItems(username);

      for (String s : inventory.keySet()) {
        pw.println(inventory.get(s) + " units of " + s);
      }
    }

  } // end of inspect command class

  public class StealCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {
      String username = tokens[3];

      if (thePlantation == null) {
        System.out.println("Can't do that: no farm selected");

        return;
      }

      if (username.equals(ownerName)) {
        pw.println("Can't do that: you ARE the owner");

        return;
      }

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      FarmLand l = thePlantation[x][y];
      Crop c = l.getCrop();
      Instant now = Instant.now();

      if (!l.isOccupied()) {
        pw.println("Nothing to steal here");
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

        pw.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
      } else if (c.getCropStatus() == 5) {
        // crop withered
        pw.println("You have left your plant neglected for too long, plow and star over");
      } else {
        // cannot harvest yet
        pw.println("Crop cannot be harvested yet, you should work harder");
      }

      // save after every command
      saveFarm();
    }

  } // end of steal command class

  // mutators ------------------------------------------------------------
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

  public Command getPlantCommand() {
    return plantCommand;
  }

  public Command getPlowCommand() {
    return plowCommand;
  }

  public Command getShowCommand() {
    return showCommand;
  }

  public Command getWaterCommand() {
    return waterCommand;
  }

  public Command getHarvestCommand() {
    return harvestCommand;
  }

  public Command getInspectCommand() {
    return inspectCommand;
  }

  // ---------------------------------------------------------------------------------

} // end of class
