package edu.brown.cs.student.farm;

import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.student.crops.ACrop;
import edu.brown.cs.student.repl.REPL;

public class FarmViewerTest {
  FarmViewer app;
  PrintWriter pw = new PrintWriter(System.out);

  @Before
  public void setUp() throws Exception {
    REPL repl = new REPL(System.in);
    // init app
    app = new FarmViewer(repl, "JUnitTestFarm");

    FarmLand[][] newFarm = new FarmLand[1][2];
    for (int i = 0; i < newFarm.length; i++) {
      for (int j = 0; j < newFarm[0].length; j++) {
        newFarm[i][j] = new FarmLand();
      }
    }

    app.setThePlantation(newFarm);
    app.saveFarm();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void plantThenWater() throws InterruptedException {
    String[] xy = {
        "0", "0"
    };
    String[] empty = {};

    plow(xy);
    plant(xy);
    water(xy);
    assertTrue(cropAt(0, 0).getDurationUntilNextStage().equals(Duration.ofSeconds(3)));
    assertTrue(cropAt(0, 0).getCropStatus() == 0);

    Thread.sleep(10000);

    show();
    assertTrue(cropAt(0, 0).getCropStatus() == 2);
    assertTrue(cropAt(0, 0).getDurationUntilNextStage().equals(Duration.ofSeconds(4)));
  }

  // helpers
  void show() {
    app.showFarm();
  }

  void plow(String[] tokens) {
    app.new PlowCommand().execute(tokens, pw);
  }

  void plant(String[] tokens) {
    app.new PlantCommand().execute(tokens, pw);
  }

  void water(String[] tokens) {
    app.new WaterCommand().execute(tokens, pw);
  }

  ACrop cropAt(int x, int y) {
    return app.getThePlantation()[x][y].getCrop();
  }
  // ---------------------------------------------------------
}
