package edu.brown.cs.student.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.student.farmTrial.FarmLand;

public class Tomato implements Crop, java.io.Serializable {
  private FarmLand farmLand;
  private String name;
  private int id;
  private int cropStatus;
  private Set<String> desiredTerrain;
  private Duration[] lifeCycleTimes;
  private Duration durationUntilNextStage;
  private Duration witherDuration;
  private Instant witheredInstant;
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
    // set land isOccupied to true, since this crop exists
    l.setIsOccupied(true);

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
    lifeCycleTimes[2] = Duration.ofSeconds(6);
    lifeCycleTimes[3] = Duration.ofSeconds(5);
    lifeCycleTimes[4] = Duration.ofSeconds(600);

    // first duration
    durationUntilNextStage = lifeCycleTimes[0];

    // default wither duration for each stage except harvest
    witherDuration = Duration.ofSeconds(10);

    // place holder: auto wither time from seeded stage
    witheredInstant = now.plus(witherDuration);

    // time next stage
    if (farmLand.isWatered(now)) {
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
    // if this crop has not grown into "stealable"
    if (cropStatus < 4) {
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

  public void wither(Instant now) {
    cropStatus = 5;
    stopGrowing();
    System.out.println("Crop automatically withered at " + witheredInstant);
  }

  @Override
  public boolean updateStatus(Instant now) {
    boolean isChanged = false;

    // a better way to deal with "seed in a dry land":
    // if instantNextStage == min, then don't update at all (i.e. skip)
    if (nextStageInstant.equals(Instant.MIN)) {
      if (now.isAfter(witheredInstant)) {
        wither(witheredInstant);

        return true;
      } else {
        return isChanged;
      }
    }

    // if already withered, no need to update
    if (cropStatus == 5) {
      // put this in here just in case (should not be necessary)
      stopGrowing();

      return isChanged;
    }

    // if timer is up
    // keep checking just in case crop progressed multiple stages since last update
    while (now.isAfter(nextStageInstant)) {

      // if crop is not withered, move onto the next stage
      // update status to next
      cropStatus += 1;

      //
      //
      //
      // print the instant
      System.out.println("Crop grew from stage " + String.valueOf(cropStatus - 1) + " to "
          + cropStatus + " at " + nextStageInstant);
      //
      //
      //

      // crop had withered, stop growing and return
      // NOTE: This is NOT a duplicate of the if statement outside the loop,
      // crop can wither as the loop repeats
      if (cropStatus == 5) {
        stopGrowing();

        return true;
      }

      // once crop is "stealable", stop growth completely and wait to wither
      // automatically
      if (cropStatus == 4) {
        nextStageInstant = witheredInstant;
        isChanged = true;

        // ignore the rest of the loop and just check whether the crop had withered
        continue;
      }

      // once crop is ready to harvest, set auto withered time
      // still needs to "grow" to the next stage (stealable)
      if (cropStatus == 3) {
        // set auto withered time for harvest (currently designed to be different from
        // all other stages)
        // because this time starts as soon as crop becomes harvest
        witheredInstant = nextStageInstant.plus(lifeCycleTimes[4]);
      } else {
        // if not yet harvest, add a constant time to wither time
        witheredInstant = nextStageInstant.plus(witherDuration);
      }

      // set durationUntilNextStage for next stage
      durationUntilNextStage = lifeCycleTimes[cropStatus];

      // update nextStageInstant
      // NOTE: starting here, nextStageInstant marks the instant of the new stage
      // before this line, nextStageInstant marks the instant of the stage that had
      // been completed
      startGrowing(nextStageInstant);

      // if land is no longer watered, or crop is not in harvest
      if (cropStatus != 3 && !nextStageInstant.isBefore(farmLand.getNextDryInstant())) {
        // pause growing
        pauseGrowing(farmLand.getNextDryInstant());
      }

//      // if land is still watered AND not in harvest
//      if (cropStatus != 3 && !nextStageInstant.isBefore(farmLand.getLastDryInstant())) {
//        // pause growing
//        pauseGrowing(farmLand.getLastDryInstant());
//      }

      isChanged = true;
    }

    // if crop is neglected for enough time IN ANY STAGE,
    // automatically wither
    if (now.isAfter(witheredInstant)) {
      wither(witheredInstant);

      return true;
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
