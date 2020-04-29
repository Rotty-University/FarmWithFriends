package edu.brown.cs.student.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import edu.brown.cs.student.farm.FarmLand;

public abstract class ACrop implements java.io.Serializable {
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
  private double sproutInfestChance;
  private double matureInfestChance;
  private boolean isSproutInfested;
  private boolean isMatureInfested;

  public ACrop(FarmLand l, int currentStatus) {
    // record instant this crop is grown
    Instant now = Instant.now();

    // SET CHILD CLASS VARIABLES---------------------------

    // initialize this crop's name
    initName();

    // init this crop's ID
    initID();

    // init this crop's terrain set
    initDesiredTerrain();

    // init this crop's lifeCycleTimes
    initLifeCycleTimes();

    // default wither duration for each stage except harvest
    initWitherDuration();

    // init min and max yield
    initMinMaxYield();

    // init max harvest times
    initMaxHarvestTimes();

    // init probabilities this crop gets infected
    initInfestChances();

    // ----------------------------------------------------

    // SET PARENT CLASS VARIABLES -------------------------

    // bind this crop to its land, like a slave basically
    farmLand = l;

    // 0: seeded or 2: mature (for multiharvest crops)
    cropStatus = currentStatus;

    // default currentHarvestTimes to max
    currentHarvestTimes = maxHarvestTimes;

    // first duration
    durationUntilNextStage = lifeCycleTimes[cropStatus];

    // auto wither time from seeded stage
    witheredInstant = now.plus(witherDuration);

    // time next stage
    if (farmLand.isWatered(now)) {
      // watered, start timer
      startGrowing(now);
    } else {
      // not watered, start growing AS SOON AS it's watered
      stopGrowing();
    }

    // randomly generate yield
    yield = (int) (Math.random() * (maxYield - minYield + 1)) + minYield;

    // determine whether this crop will be infested
    isSproutInfested = Double.compare(Math.random(), sproutInfestChance) <= 0 ? true : false;
    isMatureInfested = Double.compare(Math.random(), matureInfestChance) <= 0 ? true : false;
    // ----------------------------------------------------
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

  // --------------------------------------------------------------

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

  // concrete setters
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

  public void setMaxHarvestTimes(int m) {
    maxHarvestTimes = m;
  }

  public void setSproutInfestChance(double d) {
    sproutInfestChance = d;
  }

  public void setMatureInfestChance(double d) {
    matureInfestChance = d;
  }

  public void setWitherDuration(Duration d) {
    witherDuration = d;
  }

  public void setCurrentHarvestTimes(int c) {
    currentHarvestTimes = c;
  }

  // -------------------------------------------------------------------

  // abstract initializers
  protected abstract void initName();

  protected abstract void initID();

  protected abstract void initDesiredTerrain();

  protected abstract void initLifeCycleTimes();

  protected abstract void initWitherDuration();

  protected abstract void initMinMaxYield();

  protected abstract void initMaxHarvestTimes();

  protected abstract void initInfestChances();

  // -------------------------------------------------------------------

  // abstract helper methods
  public abstract ACrop respawn();
  // -------------------------------------------------------------------

} // end of class
