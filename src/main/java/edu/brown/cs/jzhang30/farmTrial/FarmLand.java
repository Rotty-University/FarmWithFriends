package edu.brown.cs.jzhang30.farmTrial;

public class FarmLand implements Land {

  private String terrain;
  private int cropID;
  private int cropStatus;
  private boolean isWatered;

  public FarmLand() {
    // default constructor, construct an empty farmland
    terrain = "soil";
    // no crop
    cropID = -1;
    // empty
    cropStatus = -1;
    // clearly cannot be watered
    isWatered = false;
  }

  public FarmLand(String t, int id, int status, boolean w) {
    // construct a farmland that already has a plant in place
    terrain = t;
    cropID = id;
    cropStatus = status;
    isWatered = w;
  }

  // mutators ----------------------------------------------------------
  @Override
  public String getTerrain() {
    return terrain;
  }

  /**
   * @return the cropID
   */
  public int getCropID() {
    return cropID;
  }

  /**
   * @param cropID the cropID to set
   */
  public void setCropID(int cropID) {
    this.cropID = cropID;
  }

  /**
   * @return the cropStatus
   */
  public int getCropStatus() {
    return cropStatus;
  }

  /**
   * @param cropStatus the cropStatus to set
   */
  public void setCropStatus(int cropStatus) {
    this.cropStatus = cropStatus;
  }

  /**
   * @return the isWatered
   */
  public boolean isWatered() {
    return isWatered;
  }

  /**
   * @param isWatered the isWatered to set
   */
  public void setWatered(boolean isWatered) {
    this.isWatered = isWatered;
  }

  // -------------------------------------------------------------

}
