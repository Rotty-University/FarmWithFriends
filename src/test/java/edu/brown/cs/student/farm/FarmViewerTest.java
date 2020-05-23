package edu.brown.cs.student.farm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.student.proxy.FarmProxy;

public class FarmViewerTest {
  FarmViewer app;

  @Before
  public void setUp() throws Exception {
    FarmProxy.setUpDataBase("data/farm_simulator.sqlite3");

    String testerName = "JUnitTest";

    FarmFile nextFarmFile = FarmProxy.loadFarm(testerName);
    if (nextFarmFile == null) {
      FarmProxy.initializeFarm(testerName);

      nextFarmFile = FarmProxy.loadFarm(testerName);
    }

    // farm already exists, erase and proceed
    FarmLand[][] f = new FarmLand[1][2];

    for (int i = 0; i < f.length; i++) {
      for (int j = 0; j < f[0].length; j++) {
        f[i][j] = new FarmLand();
      }
    }

    app = new FarmViewer(testerName);
    app.setThePlantation(f);
    app.saveFarm();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void plantThenWater() throws InterruptedException {

    app.plow("JUnitTest", 0, 0);
    app.plant("JUnitTest", "demo_crop", 0, 0);
    app.water(0, 0, 10);
    Instant justNow = Instant.now();
    assertTrue(cropAt(0, 0).getDurationUntilNextStage().equals(Duration.ofSeconds(3)));
    assertTrue(cropAt(0, 0).getCropStatus() == 0);

    // wait 8 seconds
    Thread.sleep(8000);

    app.water(0, 0, 10);
    show();
    assertTrue(cropAt(0, 0).getCropStatus() == 2);
    assertTrue(cropAt(0, 0).getDurationUntilNextStage().equals(Duration.ofSeconds(4)));
    assertEquals(justNow.plus(Duration.ofSeconds(14)).getEpochSecond(),
        cropAt(0, 0).getNextStageInstant().getEpochSecond());

    // wait 10 seconds
    Thread.sleep(10000);

    show();
    assertEquals(cropAt(0, 0).getCropStatus(), 3);
    assertEquals(app.getThePlantation()[0][0].isWatered(Instant.now()), false);

    // wait 1 second, land is dry, should still move on to stage 4
    Thread.sleep(1000);

    show();
    assertEquals(cropAt(0, 0).getCropStatus(), 4);
  }

  // helpers
  void show() {
    app.showFarm();
  }

  Crop cropAt(int x, int y) {
    return app.getThePlantation()[x][y].getCrop();
  }
  // ---------------------------------------------------------
}
