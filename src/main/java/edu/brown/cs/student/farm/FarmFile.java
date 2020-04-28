package edu.brown.cs.student.farm;

import java.util.HashMap;
import java.util.Map;

public class FarmFile implements java.io.Serializable {

  private FarmLand[][] thePlantation;

  // user data
  private Map<Integer, Integer> inventory = new HashMap<>();
  private String farmName = "myFarm";
  private int ownerID;

  public FarmFile(FarmLand[][] p, Map<Integer, Integer> m, String n, int o) {
    thePlantation = p;
    inventory = m;
    farmName = n;
    ownerID = o;
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
   * @return the inventory
   */
  public Map<Integer, Integer> getInventory() {
    return inventory;
  }

  /**
   * @param inventory the inventory to set
   */
  public void setInventory(Map<Integer, Integer> inventory) {
    this.inventory = inventory;
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

  /**
   * @return the ownerID
   */
  public int getOwnerID() {
    return ownerID;
  }

  // --------------------------------------------------------------------------

}
