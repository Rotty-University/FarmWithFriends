package edu.brown.cs.jzhang30.farmTrial;

import java.time.Instant;

import edu.brown.cs.jzhang30.crops.Crop;

public class FarmLand implements Land {

  private String terrain;
  private Crop crop;
  // 0: empty, unplowed
  // 1: plowed
  // 2: watered
  // 3: occupied
  // 4: occupied and watered
  private int landStatus;
  // TODO: empty land automatically dries
  private Instant timeToDry;

  public FarmLand() {
    // default constructor, construct an empty farmland
    terrain = "soil";
    // no crop
    setCrop(null);
    // empty
    setLandStatus(0);
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
   * @return the landStatus
   */
  public int getLandStatus() {
    return landStatus;
  }

  /**
   * @param landStatus the landStatus to set
   */
  public void setLandStatus(int landStatus) {
    this.landStatus = landStatus;
  }


  // -------------------------------------------------------------

}
