package edu.brown.cs.student.farmTrial;

import java.io.PrintWriter;

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
  }

  // print the current layout of the farm
  void showFarm() {
    for (FarmLand[] l : thePlantation) {
      for (FarmLand j : l) {
        System.out
        .print(
            "[ID: " + j.getCropID() + ", status: " + j.getCropStatus()
            + "]\t");
      }

      System.out.println();
    }
  }

  // plow a land
  class PlowCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);

      thePlantation[x][y].setCropStatus(0);

      showFarm();
    }

  }

  // plant a seed
  class PlantCommand implements Command {

    @Override
    public void execute(String[] tokens, PrintWriter pw) {

      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      int id = Integer.parseInt(tokens[2]);

      if (thePlantation[x][y].getCropStatus() == -1) {
        pw.println("Please plow this land first");
        return;
      }

      if (thePlantation[x][y].getCropStatus() > 0) {
        pw.println("This land is already occupied");
        return;
      }

      thePlantation[x][y].setCropID(id);
      thePlantation[x][y].setCropStatus(1);

      showFarm();
    }

  }
} // end of class
