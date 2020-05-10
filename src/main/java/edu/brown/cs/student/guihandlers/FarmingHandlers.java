package edu.brown.cs.student.guihandlers;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import edu.brown.cs.student.farm.FarmLand;
import edu.brown.cs.student.farm.FarmViewer;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class FarmingHandlers {
  FarmViewer app;
  PrintWriter pw;
  private final Gson GSON = new Gson();

  public FarmingHandlers(FarmViewer a) {
    app = a;
    System.out.println(app.getFarmName());
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
  public class ActionHandler implements Route {

    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String row = qm.value("row");
      String col = qm.value("col");
      int action = Integer.parseInt(qm.value("action"));
      String cropName = qm.value("crop");

      int r = Integer.parseInt(row);
      int c = Integer.parseInt(col);

      String[] commands = {
          row, col, cropName
      };
      // do stuff in backend
      switch (action) {
      case 1:
        // plow
        app.new PlowCommand().execute(commands, pw);
        break;

      case 2:
        // plant
        app.new PlantCommand().execute(commands, pw);
        break;

      case 3:
        // water
        app.new WaterCommand().execute(commands, pw);
        break;

      case 4:
        // harvest
        app.new HarvestCommand().execute(commands, pw);
        break;

      default:
        // nothing yet
        break;
      }

      // get updated farm
      // return Map<"row#col", int[]>
      // array for each entry:
      // 0: isPlowed (0 false, 1 true)
      // 1: isWatered
      // 2: cropID (-9 if no crop)
      // 3: cropStatus (-9 if no crop)
      // TODO 4: time left until next stage

      Instant now = Instant.now();

      // return info of the specific tile
      FarmLand land = app.getThePlantation()[r][c];
      int[] arr = new int[5];

      arr[0] = land.isPlowed() ? 1 : 0;
      arr[1] = land.isWatered(now) ? 1 : 0;
      arr[2] = land.isOccupied() ? land.getCrop().getID() : -9;
      arr[3] = land.isOccupied() ? land.getCrop().getCropStatus() : -9;
      // TODO: update this once we have a plan
      arr[4] = 0;

      return GSON.toJson(arr);

    } // end of handle()

  } // end of ActionHandler class

  /**
   * called for updating the entire farm and display for frontend
   *
   * @author zjk97
   *
   */
  public class UpdateHandler implements Route {

    @Override
    public String handle(Request request, Response response) throws Exception {
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
    } // end of handle()
  } // end of UpdateHandler class

} // end of outer class
