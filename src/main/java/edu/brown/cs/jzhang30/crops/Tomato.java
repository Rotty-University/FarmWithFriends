package edu.brown.cs.jzhang30.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.jzhang30.farmTrial.FarmLand;

public class Tomato implements Crop {
  private FarmLand farmLand;
  private String name;
  private int id;
  private int cropStatus;
  private Set<String> desiredTerrain;
  private Duration[] lifeCycleTimes;
  private Instant instantNextStage;
  private final int minYield;
  private final int maxYield;
  private int yield;
  private int maxHarvestTimes;

  public Tomato(FarmLand l) {
    // record instant this crop is grown
    Instant now = Instant.now();

    // bind this crop to its land, like a slave basically
    farmLand = l;

    name = "Tomato";

    // place holder id
    id = 1;

    // 0: seeded
    cropStatus = 0;

    // place holder desired terrain
    desiredTerrain = new HashSet<String>();
    desiredTerrain.add("soil");

    // place holder
    lifeCycleTimes = new Duration[5];
    lifeCycleTimes[0] = Duration.ofSeconds(3);
    lifeCycleTimes[1] = Duration.ofSeconds(4);
    lifeCycleTimes[2] = Duration.ofSeconds(4);
    lifeCycleTimes[3] = Duration.ofSeconds(4);
    lifeCycleTimes[4] = Duration.ofSeconds(4);

    // time next stage
    if (farmLand.getLandStatus() == 1) {
      // not watered, start growing AS SOON AS it's watered
      instantNextStage = Instant.MIN;
    } else if (farmLand.getLandStatus() == 2) {
      // watered, start timer
      instantNextStage = now.plus(lifeCycleTimes[0]);
    } else {
      // a crop should only be instantiated if land is plowed, thus should not
      // reach this block
    }

    // min max yield
    minYield = 8;
    maxYield = 12;

    // randomly generate yield
    yield = (int) (Math.random() * (maxYield - minYield + 1)) + minYield;

    // max harvest
    maxHarvestTimes = 4;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getID() {
    return id;
  }

  @Override
  public Set<String> getDesiredTerrain() {
    return desiredTerrain;
  }

  @Override
  public Duration[] getLifeCycleTimes() {
    return lifeCycleTimes;
  }

  @Override
  public int getYield() {
    return yield;
  }

  @Override
  public int getMaxHarvestTimes() {
    return maxHarvestTimes;
  }

  @Override
  public int getCropStatus() {
    return cropStatus;
  }

  @Override
  public int getMaxYield() {
    return maxYield;
  }

  @Override
  public int getMinYield() {
    return minYield;
  }

  @Override
  public FarmLand getFarmLand() {
    return farmLand;
  }

  @Override
  public void setFarmLand(FarmLand l) {
    farmLand = l;
  }

  @Override
  public Instant getInstantNextStage() {
    return instantNextStage;
  }

  @Override
  public void setInstantNextStage(Instant i) {
    instantNextStage = i;
  }

  @Override
  public void setCropStatus(int s) {
    cropStatus = s;
  }

  @Override
  public void startGrowing(Instant now) {
    // if this crop has a valid next stage
    // TODO: think about where to stop growing (how to move from harvest to
    // stealable
    if (cropStatus < 5) {
      // AND it's time to grow
      // TODO: if infested, how to pause timer and start growing later
      if (now.isAfter(instantNextStage)) {
        instantNextStage = now.plus(lifeCycleTimes[Math.abs(cropStatus)]);
      }
    }
  }

} // end of class
