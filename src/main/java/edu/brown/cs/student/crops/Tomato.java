package edu.brown.cs.student.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.student.farmTrial.FarmLand;

public class Tomato implements Crop {
  private FarmLand farmLand;
  private String name;
  private int id;
  private int cropStatus;
  private Set<String> desiredTerrain;
  private Duration[] lifeCycleTimes;
  private Duration durationUntilNextStage;
  private Instant nextStageInstant;
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

    // first duration
    durationUntilNextStage = lifeCycleTimes[0];

    // time next stage
    if (farmLand.isWatered()) {
      // watered, start timer
      nextStageInstant = now.plus(lifeCycleTimes[0]);
    } else {
      // not watered, start growing AS SOON AS it's watered
      nextStageInstant = Instant.MIN;
    }

    // min max yield
    minYield = 8;
    maxYield = 12;

    // randomly generate yield
    yield = (int) (Math.random() * (maxYield - minYield + 1)) + minYield;

    // max harvest
    maxHarvestTimes = 4;
  }

  // Controllers ----------------------------------------------
  @Override
  public void startGrowing(Instant now) {
    // if this crop has not grown into "harvest"
    if (cropStatus < 3) {
      // TODO: if infested, how to pause timer and start growing later
      nextStageInstant = now.plus(durationUntilNextStage);
    }
  }

  public void stopGrowing() {
    nextStageInstant = Instant.MAX;
  }

  @Override
  public void pauseGrowing(Instant now) {
    // store how much time left for startGrowing
    if (!nextStageInstant.equals(Instant.MAX)) {
      durationUntilNextStage = Duration.between(now, nextStageInstant);
    }
    // set nextStageInstant to infinity
    stopGrowing();
  }

  @Override
  public boolean updateStatus(Instant now) {
    boolean isChanged = false;

    // a better way to deal with "seed in a dry land":
    // if instantNextStage == min, then don't update at all (i.e. skip)
    if (nextStageInstant.equals(Instant.MIN)) {
      return isChanged;
    }

    // if timer is up
    // keep checking just in case crop progressed multiple stages since last update
    while (now.isAfter(nextStageInstant)) {
      Instant lastStageInstant = null;
      // if crop is not withered, move onto the next stage
      if (cropStatus < 5) {
        // update status to next
        cropStatus += 1;
        // set durationUntilNextStage for next stage
        durationUntilNextStage = lifeCycleTimes[cropStatus];
        // store the instant where crop stops growing
        lastStageInstant = nextStageInstant;
        // stop growing
        stopGrowing();
      }

      // if land is still watered
      // TODO: check what should be compared to farmLand.getNextDryInstant()
      // BUG: water lasts 10 seconds but grows for total 11 seconds
      if (lastStageInstant != null && lastStageInstant.isBefore(farmLand.getNextDryInstant())) {
        // subtract amount passed since last stage from durationUntilNextStage
        durationUntilNextStage = durationUntilNextStage
            .minus(Duration.between(lastStageInstant, now));

        // start growing
        startGrowing(now);
      }

      isChanged = true;
    }

    // if crop is withered, nothing happens :(
    return isChanged;
  }

  // --------------------------------------------------------------

  // mutators
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
  public Instant getNextStageInstant() {
    return nextStageInstant;
  }

  @Override
  public void setNextStageInstant(Instant i) {
    nextStageInstant = i;
  }

  @Override
  public void setCropStatus(int s) {
    cropStatus = s;
  }

  @Override
  public Duration getDurationUntilNextStage() {
    return durationUntilNextStage;
  }

  @Override
  public void setDurationUntilNextStage(Duration d) {
    durationUntilNextStage = d;
  }

} // end of class
