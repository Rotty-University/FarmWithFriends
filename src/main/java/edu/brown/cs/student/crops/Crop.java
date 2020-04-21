package edu.brown.cs.student.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import edu.brown.cs.student.farmTrial.FarmLand;

public interface Crop {

  /**
   * Set nextStageInstant to (now + durationUntilNextStage) IF grop is not yet at
   * "harvest"
   *
   * @implNote this operation DOES NOT move corpStatus to the next stage
   * @param now the instant to start growing from
   */
  public void startGrowing(Instant now);

  /**
   * Set nextStageInstant to MAX and store amount of time left in
   * durationUntilNextStage
   *
   * @implNote if the crop is NOT currently growing, this method will do nothing
   * @param now the instant to pause at
   */
  public void pauseGrowing(Instant now);

  /**
   * Updates this crop's status to the next stage if valid
   *
   * @param now the instant to compare to
   * @return true if crop successfully moved to the next stage, false if crop
   *         failed to move on
   */
  public boolean updateStatus(Instant now);

  // mutators and accessers

  public String getName();

  public int getID();

  public Set<String> getDesiredTerrain();

  public FarmLand getFarmLand();

  public void setFarmLand(FarmLand l);

  public Instant getNextStageInstant();

  /**
   * Set instantNextStage to the specified instant
   *
   * @param i instant to set to
   */
  public void setNextStageInstant(Instant i);

  /**
   * @return -2: mature infested, -1: sprout infested, 0: seeded, 1: sprout, 2:
   *         mature, 3: harvest, 4: stealable, 5: withered
   */
  public int getCropStatus();

  public void setCropStatus(int s);

  public Duration getDurationUntilNextStage();

  public void setDurationUntilNextStage(Duration d);

  /**
   * @return (seconds for testing, hours for deployment) index 0: seeded->sprout,
   *         index 1: sprout->mature, index 2: mature->harvest, index 3:
   *         harvest->stealable, index 4: harvest->withered
   */
  public Duration[] getLifeCycleTimes();

  /**
   * @return minimum yield
   */
  public int getMinYield();

  /**
   * @return maximum yield
   */
  public int getMaxYield();

  /**
   * @return a random number between min and max yield
   */
  public int getYield();

  /**
   * @return how many times this crop can be harvested before dying
   */
  public int getMaxHarvestTimes();
}
