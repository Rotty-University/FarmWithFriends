package edu.brown.cs.jzhang30.farmTrial;

import java.util.Set;

public interface Crop {

  String getName();

  int getID();

  Set<String> getDesiredTerrain();

  double[] getLifeCycleTimes();

  int getYield();
}
