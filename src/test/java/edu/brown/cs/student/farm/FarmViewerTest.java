package edu.brown.cs.student.farm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.student.proxy.FarmProxy;

public class FarmViewerTest {
  FarmViewer app;

  String testerName = "JUnitTest";

  @Before
  public void setUp() throws Exception {
    FarmProxy.setUpDataBase("data/farm_simulator.sqlite3");

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
  public void plantThenWaterNoInfest() throws InterruptedException {

    app.plow("JUnitTest", 0, 0);
    app.plant("JUnitTest", "demo_crop", 0, 0);
    Crop crop = cropAt(0, 0);
    assertEquals(crop.getNextStageInstant(), Instant.MAX);
    app.water(0, 0, 10);
    Instant justNow = Instant.now();
    assertTrue(crop.getDurationUntilNextStage().equals(Duration.ofSeconds(3)));
    assertTrue(crop.getCropStatus() == 0);

    // wait 8 seconds
    Thread.sleep(8000);

    app.water(0, 0, 10);
    show();
    assertTrue(crop.getCropStatus() == 2);
    assertTrue(crop.getDurationUntilNextStage().equals(Duration.ofSeconds(4)));
    assertEquals(justNow.plus(Duration.ofSeconds(14)).getEpochSecond(),
        crop.getNextStageInstant().getEpochSecond());

    // wait 10 seconds
    Thread.sleep(10000);

    show();
    assertEquals(crop.getCropStatus(), 3);
    assertEquals(app.getThePlantation()[0][0].isWatered(Instant.now()), false);

    // wait 1 second, land is dry, should still move on to stage 4
    Thread.sleep(1000);

    show();
    assertEquals(crop.getCropStatus(), 4);
  }

  @Test
  public void plantThenWaterInfested() throws InterruptedException {
    app.plow("JUnitTest", 0, 0);
    app.plant("JUnitTest", "demo_crop2", 0, 0);
    Crop crop = cropAt(0, 0);
    app.water(0, 0, 10);
    Instant justNow = Instant.now();
    assertTrue(crop.getDurationUntilNextStage().equals(Duration.ofSeconds(3)));
    assertTrue(crop.getCropStatus() == 0);

    // get sprout infest duration
    long sproutInfestLong = crop.getDurationUntilSproutInfested().toMillis();
    Thread.sleep(3000 + sproutInfestLong);

    show();
    Instant sproutInfestInstant = crop.getSproutInfestedInstant();
    // crop is sprout infested status
    show();
    assertTrue(crop.getCropStatus() == -1);
    assertEquals(Instant.now().getEpochSecond(), sproutInfestInstant.getEpochSecond());

    Thread.sleep(7000);
    assertFalse(app.getThePlantation()[0][0].isWatered(Instant.now()));
    app.water(0, 0, 10);
    assertEquals(crop.getDurationUntilNextStage(),
        crop.getLifeCycleTimes()[1].minus(crop.getDurationUntilSproutInfested()));
    assertEquals(crop.getNextStageInstant(), Instant.MAX);
    app.cure(testerName, 0, 0);

    Thread.sleep(crop.getDurationUntilNextStage().toMillis() + 50);
    show();
    assertTrue(app.getThePlantation()[0][0].isWatered(Instant.now()));
    assertEquals(crop.getCropStatus(), 2);
  }

  @Test
  public void cureThenWater() throws InterruptedException {
    app.plow("JUnitTest", 0, 0);
    app.plant("JUnitTest", "demo_crop2", 0, 0);
    Crop crop = cropAt(0, 0);
    app.water(0, 0, 10);
    Instant justNow = Instant.now();
    assertTrue(crop.getDurationUntilNextStage().equals(Duration.ofSeconds(3)));
    assertTrue(crop.getCropStatus() == 0);

    // get sprout infest duration
    long sproutInfestLong = crop.getDurationUntilSproutInfested().toMillis();
    Thread.sleep(3000 + sproutInfestLong);

    show();
    Instant sproutInfestInstant = crop.getSproutInfestedInstant();
    // crop is sprout infested status
    show();
    assertTrue(crop.getCropStatus() == -1);
    assertEquals(Instant.now().getEpochSecond(), sproutInfestInstant.getEpochSecond());

    Thread.sleep(7000);
    assertFalse(app.getThePlantation()[0][0].isWatered(Instant.now()));
    assertEquals(crop.getDurationUntilNextStage(),
        crop.getLifeCycleTimes()[1].minus(crop.getDurationUntilSproutInfested()));
    assertEquals(crop.getNextStageInstant(), Instant.MAX);

    app.cure(testerName, 0, 0);
    assertEquals(crop.getCropStatus(), 1);
    assertFalse(app.getThePlantation()[0][0].isWatered(Instant.now()));
    assertEquals(crop.getDurationUntilNextStage(),
        crop.getLifeCycleTimes()[1].minus(crop.getDurationUntilSproutInfested()));
    assertEquals(crop.getNextStageInstant(), Instant.MAX);

    Thread.sleep(2000);

    app.water(0, 0, 10);
    Thread.sleep(crop.getDurationUntilNextStage().toMillis() + 50);
    show();
    assertTrue(app.getThePlantation()[0][0].isWatered(Instant.now()));
    assertEquals(crop.getCropStatus(), 2);
  }

  @Test
  public void waterThenPlant() throws InterruptedException {
    app.plow("JUnitTest", 0, 0);
    app.water(0, 0, 10);

    Thread.sleep(2000);

    app.plant("JUnitTest", "demo_crop2", 0, 0);
    show();
    Crop crop = cropAt(0, 0);
    Instant justNow = Instant.now();
    assertEquals(crop.getNextStageInstant().getEpochSecond(),
        justNow.plus(crop.getLifeCycleTimes()[0]).getEpochSecond());
    assertTrue(crop.getCropStatus() == 0);

    // get sprout infest duration
    long sproutInfestLong = crop.getDurationUntilSproutInfested().toMillis();
    Thread.sleep(3000 + sproutInfestLong);

    show();
    Instant sproutInfestInstant = crop.getSproutInfestedInstant();
    // crop is sprout infested status
    show();
    assertTrue(crop.getCropStatus() == -1);
    assertEquals(Instant.now().getEpochSecond(), sproutInfestInstant.getEpochSecond());
  }

  // --------------------------------------------------------------------------

  // helpers
  void show() {
    app.showFarm();
  }

  Crop cropAt(int x, int y) {
    return app.getThePlantation()[x][y].getCrop();
  }
  // ---------------------------------------------------------
}
