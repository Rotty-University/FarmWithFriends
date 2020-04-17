package edu.brown.cs.jzhang30.farmTrial;

import java.io.PrintWriter;
import java.time.Instant;

import edu.brown.cs.jzhang30.crops.Crop;
import edu.brown.cs.jzhang30.crops.Tomato;
import edu.brown.cs.jzhang30.repl.Command;
import edu.brown.cs.jzhang30.repl.REPL;

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
    repl.register("show", new ShowCommand());
  }

  // print the current layout of the farm
  void showFarm() {
    for (FarmLand[] l : thePlantation) {
      for (FarmLand j : l) {
        if (j.getLandStatus() == 0) {
          System.out.print("[ FARMLAND ]");
        } else if (j.getLandStatus() == 1) {
          System.out.print("[ PLOWED ]");
        }
        else {

          // update crop status if necessary
          Instant now = Instant.now();
          Crop c = j.getCrop();

          if (now.compareTo(c.getInstantNextStage()) == 1) {
            int nextStatus = c.getCropStatus();

            if (nextStatus < 5) {
              c.setCropStatus(nextStatus + 1);
              c.setInstantNextStage(now.plus(c.getLifeCycleTimes()[nextStatus]));
            }
          }
          //---------------------------------------------------------------

          System.out
          .print(
              "[ID: " + c.getID() + ", status: " + c.getCropStatus()
              + "]\t");
        }
      }

      System.out.println();
    }
  }

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

      if (thePlantation[x][y].getLandStatus() == 0) {
        pw.println("Please plow this land first");
        return;
      }

      if (thePlantation[x][y].getLandStatus() > 1) {
        pw.println("This land is already occupied");
        return;
      }

      thePlantation[x][y].setCrop(new Tomato());
      thePlantation[x][y].setLandStatus(2);

      showFarm();
    }

  }
} // end of class
