package edu.brown.cs.student.farmTrial;

import java.time.Duration;
import java.time.Instant;

import edu.brown.cs.student.crops.Crop;

public class FarmLand implements Land, java.io.Serializable {
  private String terrain;
  private Crop crop;
  private boolean isPlowed;
  private Instant lastDryInstant;
  private Instant nextDryInstant;

  public FarmLand() {
    // mark the time this farm land is created
    Instant now = Instant.now();
    // default constructor, construct an empty farmland
    terrain = "soil";
    // no crop
    setCrop(null);
    // not plowed
    setIsPlowed(false);
    // last dry instant defaults to now
    setLastDryInstant(now);
    // next dry instant defaults to now
    setNextDryInstant(now);
  }

  /**
   * @param now the instant to compare to
   * @return true if this land dries, false if not
   */
  public boolean updateWaterStatus(Instant now) {
    if (now.isAfter(nextDryInstant)) {
      // update last time this land was dry
      lastDryInstant = nextDryInstant;

      return true;
    }

    return false;
  }

  /**
   * @param now           the instant the land was watered
   * @param durationToDry how much longer to keep this land wet
   * @return true if water status changes, false if not
   */
  public boolean water(Instant now, Duration durationToDry) {
    if (isOccupied()) {
      // NOTE: order matters here, don't move this down
      // update crop status before growing first
      crop.updateStatus(now);
    }

    if (isWatered(now)) {
      // already watered, only update next time to dry
      nextDryInstant = now.plus(durationToDry);

      return false;
    }

    // currently dry, update last time it's dry (now)
    lastDryInstant = now;
    // update next time to dry
    nextDryInstant = now.plus(durationToDry);

    // crop starts growing if
    // (1) there is a crop and
    // (2) (crop is pausing OR seeded on dry land) and
    // (3) it's not infested
    // TODO: add infested condition
    if (isOccupied() && (crop.getNextStageInstant().equals(Instant.MAX)
        || crop.getNextStageInstant().equals(Instant.MIN))) {
      crop.startGrowing(now);
    }

    return true;
  }

  // mutators ----------------------------------------------------------
  @Override
  public String getTerrain() {
    return terrain;
  }

  /**
   * @return the crop
   */
  public Crop getCrop() {
    return crop;
  }

  /**
   * @param crop the crop to set
   */
  public void setCrop(Crop crop) {
    this.crop = crop;
  }

  /**
   * @return true if land is plowed
   */
  public boolean isPlowed() {
    return isPlowed;
  }

  /**
   * @param p whether the land is plowed
   */
  public void setIsPlowed(boolean p) {
    isPlowed = p;
  }

  /**
   * @return the isWatered
   */
  public boolean isWatered(Instant now) {
    return now.isBefore(nextDryInstant);
  }

  /**
   * @return true if land is occupied, false if not
   */
  public boolean isOccupied() {
    return crop != null;
  }

  /**
   * @return the lastDryInstant
   */
  public Instant getLastDryInstant() {
    return lastDryInstant;
  }

  /**
   * @param lastDryInstant the lastDryInstant to set
   */
  public void setLastDryInstant(Instant lastDryInstant) {
    this.lastDryInstant = lastDryInstant;
  }

  /**
   * @return the nextDryInstant
   */
  public Instant getNextDryInstant() {
    return nextDryInstant;
  }

  /**
   * @param nextDryInstant the nextDryInstant to set
   */
  public void setNextDryInstant(Instant nextDryInstant) {
    this.nextDryInstant = nextDryInstant;
  }

  // -------------------------------------------------------------

}
