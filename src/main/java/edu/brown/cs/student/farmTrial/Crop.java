package edu.brown.cs.student.farmTrial;

import java.util.Set;

public interface Crop {

  String getName();

  int getID();

  Set<String> getDesiredTerrain();

  double[] getLifeCycleTimes();

  int getYield();
}
