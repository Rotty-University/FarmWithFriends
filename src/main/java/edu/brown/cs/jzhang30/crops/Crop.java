package edu.brown.cs.jzhang30.crops;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import edu.brown.cs.jzhang30.farmTrial.Land;

public interface Crop {

  public String getName();

  public int getID();

  public Set<String> getDesiredTerrain();

  public Land getLand();

  public void setLand(Land l);

  public Instant getInstantNextStage();

  public void setInstantNextStage(Instant i);

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
   * @return index 0: minimum yields, index 1: maximum yields
   */
  public int[] getMinMaxYields();

  /**
   * @return a random number between min and max yield
   */
  public int getYield();

  /**
   * @return how many times this crop can be harvested before dying
   */
  public int getMaxHarvestTimes();
}
