package edu.brown.cs.jzhang30.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.jzhang30.farmTrial.Land;

public class Tomato implements Crop {
  private String name;
  private int id;
  private int cropStatus;
  private Set<String> desiredTerrain;
  private Duration[] lifeCycleTimes;
  private Instant instantNextStage;
  private int[] minMaxYields;
  private int yield;
  private int maxHarvestTimes;
  private Land land;

  public Tomato() {
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
    lifeCycleTimes[0] = Duration.ofSeconds(1);
    lifeCycleTimes[1] = Duration.ofSeconds(1);
    lifeCycleTimes[2] = Duration.ofSeconds(1);
    lifeCycleTimes[3] = Duration.ofSeconds(1);
    lifeCycleTimes[4] = Duration.ofSeconds(1);
    // time next stage
    instantNextStage = Instant.now().plus(lifeCycleTimes[0]);
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
  public int[] getMinMaxYields() {
    return minMaxYields;
  }

  @Override
  public Land getLand() {
    return land;
  }

  @Override
  public Instant getInstantNextStage() {
    return instantNextStage;
  }

  @Override
  public void setLand(Land l) {
    land = l;

  }

  @Override
  public void setInstantNextStage(Instant i) {
    instantNextStage = i;

  }

  @Override
  public void setCropStatus(int s) {
    cropStatus = s;

  }

}
