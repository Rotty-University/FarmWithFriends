package edu.brown.cs.student.guihandlers;

import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.brown.cs.student.farmTrial.FarmLand;
import edu.brown.cs.student.farmTrial.FarmTrialApp;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class FarmingHandlers {
  FarmTrialApp app;
  PrintWriter pw;
  private final Gson GSON = new GsonBuilder()
      .setExclusionStrategies(new AnnotationExclusionStrategy()).create();

  public FarmingHandlers(FarmTrialApp a) {
    app = a;
    pw = new PrintWriter(System.out);
  }

  public class FarmingHandler implements Route {

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

      FarmLand[][] newFarm = app.getThePlantation();
      Map<String, int[]> hm = new HashMap<>();
      Instant now = Instant.now();

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
    }

  }

  // Custom @Exclude tag
  // adapted from contribution made by user "pkk" on stackoverflow
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Exclude {
  }

  public class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> c) {
      return false;
    }
  }
  // -------------------------------------------------------------

} // end of class
