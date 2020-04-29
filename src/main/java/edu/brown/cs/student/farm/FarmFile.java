package edu.brown.cs.student.farm;

public class FarmFile implements java.io.Serializable {

  // land and crop data
  private FarmLand[][] thePlantation;

  // user data
  private String ownerName;
  private String farmName; // default name empty string

  public FarmFile(FarmLand[][] p, String ownerName, String farmName) {
    thePlantation = p;
    this.ownerName = ownerName;
    this.farmName = farmName;
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
   * @return the farm's owner name
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * @param id the new owner's name
   */
  public void setOwnerName(String name) {
    ownerName = name;
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
