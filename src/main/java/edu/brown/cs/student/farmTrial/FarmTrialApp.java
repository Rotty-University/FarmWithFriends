package edu.brown.cs.student.farmTrial;

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
  private FarmLand[][] thePlantation = new FarmLand[1][4];

  // user data
  private Map<Integer, Integer> inventory = new HashMap<>();

  public FarmTrialApp(REPL repl) {

    // initialize the plantation
    for (int i = 0; i < thePlantation.length; i++) {
      for (int j = 0; j < thePlantation[i].length; j++) {
        thePlantation[i][j] = new FarmLand();
      }
    }

    System.out.println("Welcome to my farm");

    repl.register("plant", new PlantCommand());
    repl.register("plow", new PlowCommand());
    repl.register("s", new ShowCommand());
    repl.register("water", new WaterCommand());
    repl.register("harvest", new HarvestCommand());
  }

  // Helper methods ------------------------------------------------------------
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
    }
  }

  // plow a land
  class PlowCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);

      if (thePlantation[x][y].isOccupied()) {
        pw.println("Don't plow the plant, your nics worked hard on it");
        return;
      }

      thePlantation[x][y].setIsPlowed(true);

      showFarm();
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
      // this is already done by crop constructor, but here for reference
      l.setIsOccupied(true);

      showFarm();
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
        pw.println("Can't harvest here, your nics didn't plant anything");
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
        l.setIsOccupied(false);

        pw.println("Successfully harvested " + yield + " " + c.getName() + "(s)");
        return;
      } else {
        // cannot harvest
        pw.println("Crop cannot be harvested yet, your nics should work harder");
      }
    }
  } // end of harvest command class

  // mutators
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
