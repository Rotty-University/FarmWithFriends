package edu.brown.cs.student.crops;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.student.farmTrial.FarmLand;

public class SingleHarvestCrop extends ACrop {

  public SingleHarvestCrop(FarmLand l) {
    super(l);
  }

  @Override
  protected void initName() {
    setName("Demo Crop");
  }

  @Override
  protected void initID() {
    setID(6969);
  }

  @Override
  protected void initDesiredTerrain() {
    Set<String> s = new HashSet<>();
    s.add("soil");
    setDesiredTerrains(s);
  }

  @Override
  protected void initLifeCycleTimes() {
    Duration[] lifeCycleTimes = new Duration[5];
    lifeCycleTimes[0] = Duration.ofSeconds(3);
    lifeCycleTimes[1] = Duration.ofSeconds(4);
    lifeCycleTimes[2] = Duration.ofSeconds(6);
    lifeCycleTimes[3] = Duration.ofSeconds(5);
    lifeCycleTimes[4] = Duration.ofSeconds(600);

    setLifeCycleTimes(lifeCycleTimes);
  }

  @Override
  protected void initWitherDuration() {
    setWitherDuration(Duration.ofSeconds(120));
  }

  @Override
  protected void initMinMaxYield() {
    setMinYield(3);
    setMaxYield(8);
  }

  @Override
  protected void initMaxHarvestTimes() {
    setMaxHarvestTimes(1);
  }

  @Override
  protected void initInfestChances() {
    setSproutInfestChance(0.2);
    setMatureInfestChance(0.75);
  }

}
