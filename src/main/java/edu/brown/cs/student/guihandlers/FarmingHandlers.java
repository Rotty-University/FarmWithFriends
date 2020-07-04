package edu.brown.cs.student.guihandlers;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import edu.brown.cs.student.farm.FarmLand;
import edu.brown.cs.student.farm.FarmViewer;
import edu.brown.cs.student.proxy.FarmProxy;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

public class FarmingHandlers {
  FarmViewer app;
  PrintWriter pw;
  private final Gson GSON = new Gson();

  public FarmingHandlers(FarmViewer a) {
    app = a;
    System.out.println("new farming handlers created for: " + app.getOwnerName());
    pw = new PrintWriter(System.out);
  }

  /**
   * This method will set the app instance that the post requests will be working
   * with.
   *
   * @param a the app instance of the current user.
   */
  public void setApp(FarmViewer a) {
    app = a;
  }

  /**
   * Takes request from GUI and perform actions on the farm, then return the new
   * state of the farm
   *
   * @author zjk97
   * @author hlucco
   *
   */
  public String handleActions(Request req, Response res) {
    String username = req.session().attribute("username");

    QueryParamsMap qm = req.queryMap();
    int row = Integer.parseInt(qm.value("row"));
    int col = Integer.parseInt(qm.value("col"));
    int action = Integer.parseInt(qm.value("action"));

    // use this var to tell frontend if a steal happened or not and how much was
    // stolen (default -100 if no stealing happened)
    int stealStatus = -100;

    // TODO: decide what to do with this
    int cureStatus = -100;

    // do stuff in backend
    switch (action) {
    case 1:
      // plow
      if (!username.equals(app.getOwnerName())) {
        // trying to operate on farm that doesn't belong to current user
        return GSON.toJson(0);
      }

      app.plow(username, row, col);
      break;

    case 2:
      // plant
      if (!username.equals(app.getOwnerName())) {
        // trying to operate on farm that doesn't belong to current user
        return GSON.toJson(0);
      }

      String cropName = qm.value("crop");
      app.plant(username, cropName, row, col);
      break;

    case 3:
      // water
      if (!username.equals(app.getOwnerName())) {
        // trying to operate on farm that doesn't belong to current user
        return GSON.toJson(0);
      }

      int durationInSeconds = Integer.parseInt(qm.value("waterDuration"));
      app.water(row, col, durationInSeconds);;
      break;

    case 4:
      // harvest
      if (!username.equals(app.getOwnerName())) {
        // trying to operate on farm that doesn't belong to current user
        return GSON.toJson(0);
      }

      app.harvest(username, row, col);
      break;

    case 5:
      stealStatus = app.steal(username, row, col);
      break;

    case 6:
      cureStatus = app.cure(username, row, col);

    default:
      // nothing yet
      break;
    }

    // get updated farm
    // array for each entry:
    // 0: isPlowed (0 false, 1 true)
    // 1: isWatered
    // 2: cropID (-9 if no crop)
    // 3: cropStatus (-9 if no crop)
    // TODO 4: time left until next stage
    // 5: stealsStatus (see FarmViewer for coding)

    // return info of the specific tile
    FarmLand land = app.getThePlantation()[row][col];
    int[] arr = new int[6];

    arr[0] = land.isPlowed() ? 1 : 0;
    arr[1] = land.isWatered(Instant.now()) ? 1 : 0;
    arr[2] = land.isOccupied() ? land.getCrop().getID() : -9;
    arr[3] = land.isOccupied() ? land.getCrop().getCropStatus() : -9;
    // TODO: update this once we have a plan
    arr[4] = 0;
    arr[5] = stealStatus;

    return GSON.toJson(arr);

  } // end of handleActions()

  /**
   * called for updating the entire farm and display for frontend
   *
   * @author zjk97
   *
   */
  public String handleUpdates(Request request, Response response) {
    // update backend
    app.updateFarm();

    // send new status back to frontend
    FarmLand[][] newFarm = app.getThePlantation();
    Map<String, int[]> hm = new HashMap<>();
    Instant now = Instant.now();

    // return info of entire farm
    for (int i = 0; i < newFarm.length; i++) {
      for (int j = 0; j < newFarm[0].length; j++) {
        FarmLand land = newFarm[i][j];
        // for each land, create an array
        String key = i + "#" + j;
        int[] arr = new int[5];

        arr[0] = land.isPlowed() ? 1 : 0;
        arr[1] = land.isWatered(now) ? 1 : 0;
        arr[2] = land.isOccupied() ? land.getCrop().getID() : -9;
        arr[3] = land.isOccupied() ? land.getCrop().getCropStatus() : -9;
        // TODO: update this once we have a plan
        arr[4] = 0;

        // add this array to the return list
        hm.put(key, arr);
      }
    }

    return GSON.toJson(hm);
  } // end of handleUpdates()

  /**
   * load all inventory items from database
   * 
   * @param request  the request
   * @param response the response
   * @return String representing the Json object
   */
  public String loadInventoryItems(Request request, Response response) {
    String username = request.session().attribute("username");

    return GSON.toJson(FarmProxy.getAllToolsByUsername(username));
  } // end of loadInventoryItems()

  /**
   * load shortcut tools from database
   * 
   * @param request  the request
   * @param response the response
   * @return String representing the Json object
   */
  public String loadShortcutTools(Request request, Response response) {
    String username = request.session().attribute("username");

    return GSON.toJson(FarmProxy.getShortcutToolsByUsername(username));
  }

} // end of outer class
