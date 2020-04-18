package edu.brown.cs.jzhang30.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import edu.brown.cs.jzhang30.farmTrial.FarmLand;

public interface Crop {

  public String getName();

  public int getID();

  public Set<String> getDesiredTerrain();

  public FarmLand getFarmLand();

  public void setFarmLand(FarmLand l);

  public Instant getInstantNextStage();

  /**
   * Set instantNextStage to the specified instant
   *
   * @param i instant to set to
   */
  public void setInstantNextStage(Instant i);

  /**
   * Set instantNextStage to (now + lifeCycleTimes[Math.abs(cropStatus)])
   *
   * @implNote this operation DOES NOT move corpStatus to the next stage
   * @implNote if crop is infested, this method will use absolute value to find
   *           the right stage and keep growing towards the next stage, i.e. -2
   *           (mature infested) becomes 2 (mature)
   * @param now the instant to start growing from
   */
  public void startGrowing(Instant now);

  /**
   * @return -2: mature infested, -1: sprout infested, 0: seeded, 1: sprout, 2:
   *         mature, 3: harvest, 4: stealable, 5: withered
   */
  public int getCropStatus();

  public void setCropStatus(int s);

  /**
   * @return (seconds for testing, hours for deployment) index 0:
   *         seeded->sprout, index 1: sprout->mature, index 2: mature->harvest,
   *         index 3: harvest->stealable, index 4: harvest->withered
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
