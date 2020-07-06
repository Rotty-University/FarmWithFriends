package edu.brown.cs.student.guihandlers;

import java.util.Map;

import com.google.gson.Gson;

import edu.brown.cs.student.proxy.FarmProxy;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

public final class UserDataHandlers {
  private UserDataHandlers() {
  }

  private final static Gson GSON = new Gson();

  /**
   * Return dimensions of input user's inventory
   *
   * @param request  the request
   * @param response the response
   * @return Json representation of the dimension array
   */
  public static String loadInventoryDimensions(Request request, Response response) {
    String username = request.session().attribute("username");

    return GSON.toJson(FarmProxy.getInventorySizeByUsername(username));
  }

  public static String updateShortcutTool(Request request, Response response) {
    String username = request.session().attribute("username");
    QueryParamsMap qm = request.queryMap();

    FarmProxy.setShortcutToolsByUsername(username, Integer.parseInt(qm.value("slotNumber")),
        qm.value("newType"), qm.value("newItem"));

    return "Success";
  }

  /**
   * load all inventory items from database
   *
   * @param request  the request
   * @param response the response
   * @return String representing the Json object
   */
  public static String loadInventoryItems(Request request, Response response) {
    String username = request.session().attribute("username");

    Map<String, String[]> allItems = FarmProxy.getAllToolsByUsername(username);
    String[][] shortcuts = FarmProxy.getShortcutToolsByUsername(username);

    // remove all shortcut items from being displayed by inventory box
    for (String[] tool : shortcuts) {
      allItems.remove(tool[1]);
    }

    return GSON.toJson(allItems);
  } // end of loadInventoryItems()

  /**
   * load shortcut tools from database
   *
   * @param request  the request
   * @param response the response
   * @return String representing the Json object
   */
  public static String loadShortcutTools(Request request, Response response) {
    String username = request.session().attribute("username");

    return GSON.toJson(FarmProxy.getShortcutToolsByUsername(username));
  }

}
