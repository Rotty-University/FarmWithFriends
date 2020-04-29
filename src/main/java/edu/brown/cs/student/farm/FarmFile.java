package edu.brown.cs.student.farm;

public class FarmFile implements java.io.Serializable {

  // land and crop data
  private FarmLand[][] thePlantation;

  // user data
  private int ownerID;
  private String farmName = ""; // default name empty string

  public FarmFile(FarmLand[][] p, int ownerID) {
    thePlantation = p;
    this.ownerID = ownerID;
  }

  // mutators ----------------------------------------------------------------
  /**
   * @return the thePlantation
   */
  public FarmLand[][] getThePlantation() {
    return thePlantation;
  }

  /**
   * @param thePlantation the thePlantation to set
   */
  public void setThePlantation(FarmLand[][] thePlantation) {
    this.thePlantation = thePlantation;
  }

  /**
   * @return the farm's owner id
   */
  public int getOwnerID() {
    return ownerID;
  }

  /**
   * @param id the new owner's user id
   */
  public void setOwnerID(int id) {
    ownerID = id;
  }

  /**
   * @return the farmName
   */
  public String getFarmName() {
    return farmName;
  }

  /**
   * @param farmName the farmName to set
   */
  public void setFarmName(String farmName) {
    this.farmName = farmName;
  }

  // --------------------------------------------------------------------------

}
