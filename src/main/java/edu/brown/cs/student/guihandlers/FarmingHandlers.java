package edu.brown.cs.student.guihandlers;

import java.io.PrintWriter;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.student.farmTrial.FarmLand;
import edu.brown.cs.student.farmTrial.FarmTrialApp;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class FarmingHandlers {
  FarmTrialApp app;
  PrintWriter pw;
  private static final Gson GSON = new Gson();

  public FarmingHandlers(FarmTrialApp a) {
    app = a;
    pw = new PrintWriter(System.out);
  }

  private class FarmingHandler implements Route {

    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String row = qm.value("row");
      String col = qm.value("col");
      // TODO: make this value an int in backend
      int action = Integer.parseInt(qm.value("action"));

      String[] commands = {
          row, col
      };

      // do stuff in backend
      switch (action) {
      case 0:
        // update status
        app.new ShowCommand().execute(commands, pw);

        break;

      case 1:
        // plow
        break;

      case 2:
        // plant
        break;

      case 3:
        // water
        break;

      case 4:
        // harvest
        break;

      default:
        // nothing yet
        break;
      }

      // get updated farm
      Map<String, FarmLand[][]> newFarm = ImmutableMap.of("farm", app.getThePlantation());

      return GSON.toJson(newFarm);
    }

  }
}
