package edu.brown.cs.student.farm;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import edu.brown.cs.student.proxy.FarmProxy;

public class Crop implements java.io.Serializable {
  private FarmLand farmLand;
  private String name;
  private int id;
  private int cropStatus;
  private Set<String> desiredTerrains;
  private Duration[] lifeCycleTimes;
  private Duration durationUntilNextStage;
  private Duration witherDuration;
  private Instant witheredInstant;
  private Instant nextStageInstant;
  private int minYield;
  private int maxYield;
  private int yield;
  private int maxHarvestTimes;
  private int currentHarvestTimes;
  private int sproutInfestChance;
  private int matureInfestChance;
  private boolean isSproutInfested;
  private boolean isMatureInfested;

  public Crop(String cropName) {
    // initialize this crop's name
    name = cropName;

    // ------------------------------------------------------------------
    // everything else is initialized in FarmProxy (queried from database)
  }

  // Controllers ----------------------------------------------
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

  public boolean updateStatus(Instant now) {
    boolean isChanged = false;

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

      // TODO: add infestation time here

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

  /**
   * return a new this crop with one less currentHarvestTimes if possible,
   * otherwise return null (crop is gone for good)
   */
  public Crop respawn() {
    if (currentHarvestTimes > 1) {
      // TODO: change this to using FarmProxy
      Crop newCrop = FarmProxy.getCrop(name, farmLand, 2);
      newCrop.setCurrentHarvestTimes(currentHarvestTimes - 1);

      return newCrop;
    } else {
      return null;
    }
  }

  // -------------------------------------------------------------------

  // getters
  public String getName() {
    return name;
  }

  public int getID() {
    return id;
  }

  public Set<String> getDesiredTerrains() {
    return desiredTerrains;
  }

  public Duration[] getLifeCycleTimes() {
    return lifeCycleTimes;
  }

  public int getYield() {
    return yield;
  }

  public int getMaxHarvestTimes() {
    return maxHarvestTimes;
  }

  public int getCropStatus() {
    return cropStatus;
  }

  public int getMaxYield() {
    return maxYield;
  }

  public int getMinYield() {
    return minYield;
  }

  public FarmLand getFarmLand() {
    return farmLand;
  }

  public Instant getNextStageInstant() {
    return nextStageInstant;
  }

  public Duration getDurationUntilNextStage() {
    return durationUntilNextStage;
  }

  public int getCurrentHarvestTimes() {
    return currentHarvestTimes;
  }

  // -------------------------------------------------------------------

  // setters
  public void setFarmLand(FarmLand l) {
    farmLand = l;
  }

  public void setName(String n) {
    name = n;
  }

  public void setID(int id) {
    this.id = id;
  }

  public void setLifeCycleTimes(Duration[] d) {
    lifeCycleTimes = d;
  }

  public void setNextStageInstant(Instant i) {
    nextStageInstant = i;
  }

  public void setCropStatus(int s) {
    cropStatus = s;
  }

  public void setDurationUntilNextStage(Duration d) {
    durationUntilNextStage = d;
  }

  public void setDesiredTerrains(Set<String> s) {
    desiredTerrains = s;
  }

  public void setMinYield(int i) {
    minYield = i;
  }

  public void setMaxYield(int i) {
    maxYield = i;
  }

  public void setYield(int i) {
    yield = i;
  }

  public void setMaxHarvestTimes(int m) {
    maxHarvestTimes = m;
  }

  public void setSproutInfestChance(int d) {
    sproutInfestChance = d;
  }

  public void setMatureInfestChance(int d) {
    matureInfestChance = d;
  }

  public void setIsSproutInfested(boolean b) {
    isSproutInfested = b;
  }

  public void setIsMatureInfested(boolean b) {
    isMatureInfested = b;
  }

  public void setWitherDuration(Duration d) {
    witherDuration = d;
  }

  public void setWitheredInstant(Instant i) {
    witheredInstant = i;
  }

  public void setCurrentHarvestTimes(int c) {
    currentHarvestTimes = c;
  }

  // -------------------------------------------------------------------

} // end of class
