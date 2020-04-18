package edu.brown.cs.student.farmTrial;

import java.io.PrintWriter;
import java.time.Instant;

import edu.brown.cs.student.crops.Crop;
import edu.brown.cs.student.crops.Tomato;
import edu.brown.cs.student.repl.Command;
import edu.brown.cs.student.repl.REPL;

public class FarmTrialApp {
  // welcome to my farm
  FarmLand[][] thePlantation = new FarmLand[2][4];

  public FarmTrialApp(REPL repl) {

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
  }

  // Helper methods ------------------------------------------------------------
  // print the current layout of the farm
  void showFarm() {
    Instant now = Instant.now();

    for (FarmLand[] l : thePlantation) {
      for (FarmLand j : l) {
        if (j.getLandStatus() == 0) {
          System.out.print("[ FARMLAND ]");
        } else if (j.getLandStatus() == 1) {
          System.out.print("[ PLOWED ]");
        } else if (j.getLandStatus() == 2) {
          System.out.print("[ WATEREDLAND ]");
        }
        else {
          // there is a crop on this land
          Crop c = j.getCrop();

          // update crop if necessary
          updateCropStatus(c, now);

          System.out
          .print(
              "[Crop ID: " + c.getID() + ", land status: "
                  + j.getLandStatus() + ", crop status: "
                  + c.getCropStatus() + "] ");
        }
      }

      System.out.println();
    }
  }

  // Update crop status helper method
  void updateCropStatus(Crop c, Instant now) {
    // if crop doesn't exist, do nothing
    if (c == null) {
      return;
    }

    Instant nextStage = c.getInstantNextStage();
    int cropStatus = c.getCropStatus();

    // if timer is up AND land is watered
    // ** NOTE TO SELF: do NOT be an idiot again and remove "land is watered"
    // it's here for a reason, dumbass, stop trying to be smart
    // if you forgot why again, think: what happens when seed is planted on DRY
    // land
    if (now.isAfter(nextStage) && c.getFarmLand().getLandStatus() == 4) {
      // if crop is not withered, move onto the next stage
      if (cropStatus < 5) {
        // update status to next
        c.setCropStatus(cropStatus + 1);
      }
      // land becomes dry
      c.getFarmLand().setLandStatus(3);
      // if crop is withered, nothing happens :(
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

      if (thePlantation[x][y].getLandStatus() > 2) {
        pw.println("Don't plow the plant, your nics worked hard on it");
        return;
      }

      thePlantation[x][y].setLandStatus(1);

      showFarm();
    }
  }

  // plant a seed
  class PlantCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      int currentStatus = thePlantation[x][y].getLandStatus();

      if (currentStatus == 0) {
        pw.println("Please plow this land first");
        return;
      }

      if (currentStatus > 2) {
        pw.println("This land is already occupied");
        return;
      }

      thePlantation[x][y].setCrop(new Tomato(thePlantation[x][y]));
      thePlantation[x][y].setLandStatus(currentStatus + 2);

      showFarm();
    }
  }

  class WaterCommand implements Command {
    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      Instant now = Instant.now();

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      int currentStatus = thePlantation[x][y].getLandStatus();

      if (currentStatus == 0) {
        pw.println("Plow first then water");
        return;
      }

      // if watered, or occupied and watered, stay the same (no change)
      if (currentStatus != 2 && currentStatus < 4) {
        // else change to watered or occupied and watered
        thePlantation[x][y].setLandStatus(currentStatus + 1);
      }

      // start growing if land is NOW occupied and watered
      if (currentStatus + 1 == 4) {
        thePlantation[x][y].getCrop().startGrowing(now);
      }

      showFarm();
    }
  }

} // end of class
