package edu.brown.cs.student.guihandlers;

import java.io.PrintWriter;
import java.time.Instant;

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
   * Takes request from GUI and perform actions on the farm, then return the new
   * state of the farm
   *
   * @author zjk97
   * @author hlucco
   *
   */
  public class FarmingHandler implements Route {
    /**
     * This method will set the app instance that the post requests will be working
     * with.
     *
     * @param a the app instance of the current user.
     */
    public void setApp(FarmViewer a) {
      app = a;
    }

    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String row = qm.value("row");
      String col = qm.value("col");
      int action = Integer.parseInt(qm.value("action"));
      int r = Integer.parseInt(row);
      int c = Integer.parseInt(col);

      String[] commands = {
          row, col
      };
      // do stuff in backend
      switch (action) {
      case 0:
        // update status
        app.updateOneTile(r, c);

        break;

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

      // get updated farm tile
      // return int[]
      // array with info for this tile:
      // 0: isPlowed (0 false, 1 true)
      // 1: isWatered
      // 2: cropID (-9 if no crop)
      // 3: cropStatus (-9 if no crop)
      // TODO 4: time left until next stage

      FarmLand[][] newFarm = app.getThePlantation();
      Instant now = Instant.now();

      FarmLand land = newFarm[r][c];
      int[] arr = new int[5];

      arr[0] = land.isPlowed() ? 1 : 0;
      arr[1] = land.isWatered(now) ? 1 : 0;
      arr[2] = land.isOccupied() ? land.getCrop().getID() : -9;
      arr[3] = land.isOccupied() ? land.getCrop().getCropStatus() : -9;
      // TODO: update this once we have a plan
      arr[4] = 0;

      return GSON.toJson(arr);
    }

  }

} // end of class
