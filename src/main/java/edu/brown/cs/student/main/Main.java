package edu.brown.cs.student.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.student.farm.FarmFile;
import edu.brown.cs.student.farm.FarmViewer;
import edu.brown.cs.student.farm.FarmViewer.SwitchCommand;
import edu.brown.cs.student.guihandlers.FarmingHandlers;
import edu.brown.cs.student.guihandlers.FarmingHandlers.FarmingHandler;
import edu.brown.cs.student.proxy.FarmProxy;
import edu.brown.cs.student.repl.REPL;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static REPL repl;
  private static final Gson GSON = new Gson();
  private static FarmViewer app;
  private static FarmingHandlers farmingHandlers;

  static String message = "";
  static String createMessage = "";
  static String userCookie = null;
  static int currentMapID = 1;
  static int freeSpaceInMap;
  static FarmingHandler farmHandler;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    // Process commands in a REPL
    repl = new REPL(System.in);

//// uncomment here to use commandline only
//    // *************************************
//    // *** DO NOT DELETE***
//    // *************************************
//    // set database for commandline use
//    FarmProxy.setUpDataBase();
//    // init app
//    app = new FarmViewer(repl, "JUnitTest");
//    // init farming handlers
//    String[] tokens = {
//        "JUnitTest"
//    };
//    app.new SwitchCommand().execute(tokens, new PrintWriter(System.out));
//// -------------------------

    FarmProxy.setUpDataBase("data/farm_simulator.sqlite3");
    runSparkServer((int) options.valueOf("port"));
    initFarmViewerAndHandler();
    repl.run();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
  }

  private void runSparkServer(int port) {
    Spark.port(getHerokuAssignedPort());
//    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();
    // Setting up the spark pages
    Spark.redirect.get("/", "/login");
    Spark.get("/login", new LoginPageHandler(), freeMarker);
    Spark.post("/home", new HomePageHandler(), freeMarker);
    Spark.get("/home", new HomePageAlreadyLoggedInHandler(), freeMarker);
    Spark.get("/create_account", new CreateAccountPageHandler(), freeMarker);
    Spark.get("/new_user", new NewUserPageAlreadyLoggedInHandler(), freeMarker);
    Spark.post("/new_user_form", new NewUserPageHandler());
    Spark.get("/logout", new LogOutHandler(), freeMarker);
    Spark.post("/adding_friend", new AddingFriendsHandler());
    Spark.post("/friendLoader", new FriendLoaderHandler());
    Spark.post("/posting_trade", new TradePostHandler());
    Spark.post("/retrieve_sell", new GetInventoryHandler());
    Spark.post("/tradeLoader", new TradeLoaderHandler());
    Spark.post("/inventoryLoader", new DisplayInventoryHandler());
    Spark.post("/acceptTrade", new MakeTradeHandler());
    Spark.post("/friendPendingLoader", new FriendPendingLoaderHandler());
    Spark.post("/friendAccepted", new FriendAcceptedHandler());
    Spark.post("/mapMaker", new MapMaker());
    Spark.post("/mapRetriever", new MapRetriever());
    Spark.post("clickOnMap", new ClickOnMapHandler());
    Spark.get("/mapRetrieverForMapsComponent", new MapRetrieverForReact());
    Spark.post("showingWhatFriendWasClicked", new ClickingFriendOnMapHandler());

    // all farmingHandler routes are made in initFarmViewerAndHandler
  }

  // call this whenever someone logs in and the game starts
  private static void initFarmViewerAndHandler() {
    // baseline initialization of the post request.
    if (userCookie == null) {
      app = new FarmViewer(repl, "");
      String[] tokens = {
          userCookie
      };
      app.new SwitchCommand().execute(tokens, new PrintWriter(System.out));

      // init farming handlers
      farmingHandlers = new FarmingHandlers(app);
      farmHandler = farmingHandlers.new FarmingHandler();
      Spark.post("/farmland", farmHandler);
      return;
    }

    // current user exists, init app
    app = new FarmViewer(repl, userCookie);
    String[] tokens = {
        userCookie
    };
    app.new SwitchCommand().execute(tokens, new PrintWriter(System.out));
    farmHandler.setApp(app);
    // init farming handlers
//    farmingHandlers = new FarmingHandlers(app);
//    Spark.post("/farmland", farmingHandlers.new FarmingHandler());
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  // mutators
  /**
   * @return the current REPL object
   */
  public REPL getREPL() {
    return repl;
  }

  /**
   * Handle requests to the home page of the farm simulator where the user will
   * login. This page will contain the form for the log in.
   *
   */
  private static class LoginPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Farmulator", "message", message);
      message = "";
      // checking to make sure the user isn't already logged on. Will take to their
      // homepage if they are.
      if (req.cookies().containsKey(userCookie)) {
        message = "You are already logged in";
        res.redirect("/home");
        return new ModelAndView(null, "home.ftl");
      }

      return new ModelAndView(variables, "home.ftl");
    }
  }

  /**
   * Handles the get request for when the user is going to their home profile
   * page.
   *
   */
  private static class HomePageAlreadyLoggedInHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // checking to make sure that the user is logged in before they can access this
      // page.
      if (userCookie == null) {
        res.redirect("/login");
        return new ModelAndView(null, "home.ftl");
      }
      // if the user still hasn't picked a spot on the map, then they will be sent
      // back to the new user page.
      if (FarmProxy.getStatusOfUser(userCookie).equals("true")) {
        res.redirect("/new_user");
      }
      Map<String, Object> variables = ImmutableMap.of("title", "Farmulator");

      // create new farm for user if it doesn't exist
      FarmFile nextFarmFile = FarmProxy.loadFarm(userCookie);
      if (nextFarmFile == null) {
        // TODO: fix initializeFarm in proxy
        FarmProxy.initializeFarm(userCookie);

        nextFarmFile = FarmProxy.loadFarm(userCookie);
      }
      // init farm and start game
      initFarmViewerAndHandler();

      return new ModelAndView(variables, "user_home.ftl");
    }
  }

  /**
   * This is the handler for the creating account page. It will show the page with
   * the form for creating an account.
   *
   */
  private static class CreateAccountPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Farmulator");
      createMessage = "";
      return new ModelAndView(variables, "create_account.ftl");
    }
  }

  /**
   * This is the handler for the login form. It will redirect to the login page if
   * the log in is invalid.
   *
   */
  private static class HomePageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap data = req.queryMap();
      String username = data.value("username");
      String password = data.value("password");
      String name = FarmProxy.getUserNameFromDataBase(username);
      // if the name doesn't exist reload and raise error message.
      if (name == null) {
        message = "The user doesn't seem to exist. Please try again";
        res.redirect("/login");
        return new ModelAndView(null, "user_home.ftl");
      }
      // getting all the user information below.
      String[] userInfo = FarmProxy.getUserInfoFromDataBaseForLogIn(username);
      String hashedPasswordFromDataBase = userInfo[1];
      String saltedString = userInfo[2];
      // string cleaning here to get the salt back into bytes.
      saltedString = saltedString.strip();
      saltedString = saltedString.replace("[", "");
      saltedString = saltedString.replace("]", "");
      saltedString = saltedString.replace(",", "");
      byte[] hashedPassword = null;
      byte[] salt = new byte[16];
      String[] stringArr = saltedString.split(" ");
      for (int i = 0; i < salt.length; i++) {
        salt[i] = (byte) Integer.parseInt(stringArr[i]);
      }
      // instance of object that will hash the password
      MessageDigest md;
      try {
        // hash the password
        md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      // check if the hash from the database matches this hash
      if (Arrays.toString(hashedPassword).equals(hashedPasswordFromDataBase) == false) {
        message = "The password is incorrect. Please try again";
        res.redirect("/login");
      }
      // valid login credentials.
      userCookie = username;
      res.cookie(username, username);
      // keeping track of the logged in user.
      req.session(true);
      req.session().attribute(username, username);
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator");

      // init farm and start game
      initFarmViewerAndHandler();
      // have to redirect here if they havent picked a location on the map yet
      if (FarmProxy.getStatusOfUser(userCookie).equals("true")) {
        res.redirect("/new_user");
      }
      return new ModelAndView(variables, "user_home.ftl");
    }
  }

  /**
   * This is the handler for interpreting the form for creating a new user. The
   * post request will be from the creation of a new user page.It will send an
   * error message if there is something that is invalid. It will load the new
   * user's profile page on acceptance.
   *
   */
  private static class NewUserPageHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap data = req.queryMap();
      String email = data.value("email");
      String username = data.value("username");
      String password = data.value("password");
      String reEntered = data.value("re_password");

      // making sure the string is of the right format.
      if (!Pattern.matches("[A-Za-z][A-Za-z0-9]{5,16}", username)) {
        createMessage = "The username must start with a letter and be at least 6 characters and at most 16. Try Again";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badUser");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // making sure the two passwords equal each other.
      if (!password.equals(reEntered)) {
        createMessage = "The passwords didn't match. Try Again";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badPassword");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Making sure that the user name they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(username) != null) {
        createMessage = "The username already exists. Please try a different username.";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badUser");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Making sure that the email they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(email) != null) {
        createMessage = "The email already exists. Please try a different email.";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badEmail");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Making sure that the email they are trying to make doesn't exist already.
      if (email.equals("")) {
        createMessage = "Please input an email.";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badEmail");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      if (password.equals("") && reEntered.equals("")) {
        createMessage = "Please input a password.";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badPassword");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      if (Pattern.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+", email) == false) {
        createMessage = "Please input a valid email";
        Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
            "badEmail");
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Random number generator to generate the salt that will be used to hash.
      SecureRandom random = new SecureRandom();
      byte[] hashedPassword = null;
      byte[] salt = new byte[16];
      random.nextBytes(salt);
      MessageDigest md;
      try {
        md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      // This is where we are seeing if there is a map in the database and getting the
      // most recent one
      // to check if it is has free space the user can click on.
      String[] mapInfo = FarmProxy.getDataFromMostRecentMap();
      // only read if this is not null
      if (mapInfo != null) {
        int mapid = Integer.parseInt(mapInfo[0]);
        int freespace = Integer.parseInt(mapInfo[2]);
        // if there is no free space meaning that each user already holds a place here,
        // then make a new map by incrementing the map counter.
        if (freespace == 0) {
          // since there is no free space we increment the map counter and this is the map
          // that will belong to the user.
          currentMapID = mapid + 1;
          // else just make the currentMap the one from the database. the free space will
          // be adjusted later.
        } else {
          currentMapID = mapid;
          freeSpaceInMap = freespace;
        }
      }
      // NEED TO GET RID OF THIS.
      else {
        currentMapID = 1;
        freeSpaceInMap = -1;
      }

      // insert this user information into the database.
      // change the true back to false eventually after they pick a spot on map.
      FarmProxy.insertUserInfoIntoDatabase(username, Arrays.toString(hashedPassword),
          Arrays.toString(salt), email, currentMapID, "true");
      userCookie = username;

      res.cookie(username, username);
      // keeping track of user.
      req.session(true);
      req.session().attribute(username, username);

      // make new farm for this user if it doesn't exist
      FarmFile nextFarmFile = FarmProxy.loadFarm(userCookie);
      if (nextFarmFile == null) {
        // TODO: fix initializeFarm in proxy
        FarmProxy.initializeFarm(userCookie);

        nextFarmFile = FarmProxy.loadFarm(userCookie);
      }

      // init farm and start game
      initFarmViewerAndHandler();

      createMessage = "";
      Map<String, String> variables = ImmutableMap.of("message", createMessage, "canCreate",
          "true");
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }

  }

  /**
   * Handles the get request for when the user is going to their new_user_profile
   * page profile page if they haven't finished yet.
   *
   */
  private static class NewUserPageAlreadyLoggedInHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // checking to make sure that the user is logged in before they can access this
      // page.
      if (userCookie == null) {
        res.redirect("/login");
        return new ModelAndView(null, "home.ftl");
      }
      // checking to make sure that the user can't go back to the map page if they
      // already picked a spot there.
      if (FarmProxy.getStatusOfUser(userCookie).equals("false")) {
        res.redirect("/home");
      }
      Map<String, Object> variables = ImmutableMap.of("title", "Farmulator", "name", userCookie);
      return new ModelAndView(variables, "new_user.ftl");
    }
  }

  /**
   * Handle requests to the logout page of the farm simulator where the user will
   * logout. This page will redirect to login page
   *
   */
  private static class LogOutHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (userCookie == null) {
        res.redirect("/login");
        return new ModelAndView(null, "home.ftl");
      }
      if (req.session().attributes().contains(userCookie)) {
        res.removeCookie(userCookie);
        req.session().removeAttribute(userCookie);
        userCookie = null;
      }
      message = "You have been logged out. Thank you.";
      Map<String, Object> variables = ImmutableMap.of("title", "Farmulator", "message", message);
      return new ModelAndView(variables, "home.ftl");
    }
  }

  /**
   * This class will handle the request for adding a friend and will send a
   * message back to the javascript post request pertaining to the status of
   * adding this friend.
   */
  private static class AddingFriendsHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      // getting the text value.
      String username = qm.value("text");
      String message = "";
      Map<String, String> variables;
      // making sure the user can't add themselves.
      if (username.equals(userCookie)) {
        message = "You can't add yourself. Try again.";
        variables = ImmutableMap.of("message", message);
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Making sure that the user name they are trying add exists.
      if (FarmProxy.getUserNameFromDataBase(username) == null) {
        message = "The user doesn't exist. Try adding someone else.";
      } else {
        String friendslist = FarmProxy.getFriendsList(userCookie);
        String[] friends = friendslist.split(",");
        // check to make sure the user isn't already in the friends list.
        for (String friend : friends) {
          if (username.equals(friend)) {
            message = "This friend is already in your friends list.";
            variables = ImmutableMap.of("message", message);
            GSON.toJson(variables);
            return GSON.toJson(variables);
          }
        }
        String friendslistpending = FarmProxy.getFriendsListPending(username);
        String[] friendspending = friendslistpending.split(",");
        // check to make sure the user isn't already in the pending friends list of the
        // other user.
        for (String friend : friendspending) {
          if (userCookie.equals(friend)) {
            message = "You already sent a friend request to this person.";
            variables = ImmutableMap.of("message", message);
            GSON.toJson(variables);
            return GSON.toJson(variables);
          }
        }
        // the user is trying to send a request to somebody that already sent to them
        friendslistpending = FarmProxy.getFriendsListPending(userCookie);
        friendspending = friendslistpending.split(",");
        // check to make sure the user isn't already in the friends list.
        for (String friend : friendspending) {
          if (userCookie.equals(friend)) {
            message = "This person already sent you one. Check your pending friend requests.";
            variables = ImmutableMap.of("message", message);
            GSON.toJson(variables);
            return GSON.toJson(variables);
          }
        }
        // add this current user who is trying to add to the pending list of the user
        // they are trying to add
        FarmProxy.updateFriendsPending(userCookie, username);
        message = "Sending the request right now";
        System.out.println("adding them");
      }
      variables = ImmutableMap.of("message", message);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  private static class TradePostHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, String> variables;
      String cropS = qm.value("cSell");
      String quantS = qm.value("qSell");
      String cropB = qm.value("cBuy");
      String quantB = qm.value("qBuy");
      if (FarmProxy.getOneInventoryItem(userCookie, cropS) >= Integer.parseInt(quantS)) {

        FarmProxy.updateTradingCenter(userCookie, cropS, quantS, cropB, quantB);
        variables = ImmutableMap.of("message", "Trade Posted");
      } else {
        variables = ImmutableMap.of("message", "Not enough " + cropS + " to make this trade");
      }
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for displaying the friend's list of a user
   * when they want to see it.
   */
  private static class FriendLoaderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      String friendslist = FarmProxy.getFriendsList(userCookie);
      Map<String, String> variables = ImmutableMap.of("list", friendslist);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  private static class TradeLoaderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String tradeCenter = FarmProxy.getTradingCenter();
      StringBuilder htmlCode = new StringBuilder();
      htmlCode.append("<tr><th>Seller</th><th>Crop Selling</th>"
          + "<th>Amount</th><th>Crop Requesting</th><th>Ammount</th><th></th></tr>");
      String[] rows = tradeCenter.split(";");
      for (String r : rows) {
        htmlCode.append("<tr>");
        String[] col = r.split(",");
        for (String c : col) {
          htmlCode.append("<td>").append(c).append("</td>");
        }
        htmlCode.append("<td>").append("<button onClick=makeTrade(\"" + r + "\")>Accept</button>")
            .append("</td>");
        htmlCode.append("</tr>");
      }
      System.out.println(tradeCenter);
      Map<String, String> variables = ImmutableMap.of("list", htmlCode.toString());
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  private static class GetInventoryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      Map<String, Integer> userInventory = FarmProxy.getAllInventoryItems(userCookie);
      StringBuilder htmlCode = new StringBuilder();
      for (String r : userInventory.keySet()) {
        htmlCode.append("<option value=\"" + r + "\">" + r + "</option>");
      }
      StringBuilder htmlCode2 = new StringBuilder();
      for (String r : FarmProxy.getAllCropNames()) {
        htmlCode2.append("<option value=\"" + r + "\">" + r + "</option>");
      }
      Map<String, String> variables = ImmutableMap.of("list1", htmlCode.toString(), "list2",
          htmlCode2.toString());
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  private static class DisplayInventoryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      Map<String, Integer> userInventory = FarmProxy.getAllInventoryItems(userCookie);
      StringBuilder htmlCode = new StringBuilder();
      htmlCode.append("<tr><th>Crop</th><th>Amount</th></tr>");
      for (String r : userInventory.keySet()) {
        htmlCode.append("<tr>");
        htmlCode.append("<td>").append(r).append("</td>");
        htmlCode.append("<td>").append(userInventory.get(r)).append("</td>");
        htmlCode.append("<\tr>");
      }
      Map<String, String> variables = ImmutableMap.of("list", htmlCode.toString());
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for displaying the pending requests that
   * they can accept of a user when they want to see it.
   *
   */
  private static class FriendPendingLoaderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      String friendslist = FarmProxy.getFriendsListPending(userCookie);
      Map<String, String> variables = ImmutableMap.of("list", friendslist);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for accepting a friend when it is clicked
   * on from the pending requests and updating the friend's list of each user as
   * well the pending friends list of the current user.
   */
  private static class FriendAcceptedHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("text");
      String friendslistpending = FarmProxy.getFriendsListPending(userCookie);
      friendslistpending = friendslistpending.replace(username + ",", "");
      FarmProxy.updateFriendsPendingAfterAdding(friendslistpending, userCookie);
      FarmProxy.updateFriendsList(userCookie, username);
      FarmProxy.updateFriendsList(username, userCookie);
      Map<String, String> variables = ImmutableMap.of("list", username);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the making of the random map so that it is constant
   * for all users and will be made when the other maps are currently full.
   *
   */
  private static class MapMaker implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String dictionarydata = qm.value("dictionary_data");
      int freeSpace = Integer.parseInt(qm.value("free_space"));
      // make sure that the freeSpace passed is subtracted by one when there is
      // already something in the database unless you update it in the javascript.
//      if (freeSpaceInMap == -1) {
      freeSpaceInMap = freeSpace;
//      }
      FarmProxy.insertMapIntoDataBase(currentMapID, dictionarydata, freeSpace);
      FarmProxy.updateFreeSpaceInMaps(currentMapID, freeSpaceInMap - 1);
      Map<String, String> variables = ImmutableMap.of("data", dictionarydata);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the making of the random map so that it is constant
   * for all users.
   *
   */
  private static class MapRetriever implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int id = FarmProxy.getMapIDofUserFromDataBase(userCookie);
      String needMap = "false";
      String mapdata = FarmProxy.getMapFromDataBase(id);
      if (mapdata == null) {
        needMap = "true";
        mapdata = "{}";
      } else {
        FarmProxy.updateFreeSpaceInMaps(currentMapID, freeSpaceInMap - 1);
      }
      // Will need to pass in variable to the map that will be used to black out areas
      // taken already by players.
      Map<String, String> variables = ImmutableMap.of("data", mapdata, "mapNeeded", needMap);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle when the user has clicked on the map to have their
   * farm location. It will redirect to the home page where their farm will be set
   * up.
   *
   */
  private static class ClickOnMapHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String mapData = qm.value("dictionary_data");
      String row = qm.value("row");
      String col = qm.value("col");
      // keeping track of this users location in the map.
      FarmProxy.updateTheRowAndColumnofUserLocationInMap(userCookie, Integer.parseInt(row),
          Integer.parseInt(col));
      // Updating the map so it knows the space that is already occupied and the user
      // can't click on.
      FarmProxy.updateTheMapData(currentMapID, mapData);
      FarmProxy.updateNewUserIndication(userCookie, "false");
      Map<String, String> variables = ImmutableMap.of("data", mapData);
      res.redirect("/home");
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the making of the random map so that it is constant
   * for all users.
   *
   */
  private static class MapRetrieverForReact implements Route {
    @Override
    public String handle(Request req, Response res) {
      int id = FarmProxy.getMapIDofUserFromDataBase(userCookie);
      String mapdata = FarmProxy.getMapFromDataBase(id);
      int[] coords = FarmProxy.getRowAndColumnOfUserMapLocation(userCookie);
      String row = String.valueOf(coords[0]);
      String col = String.valueOf(coords[1]);
      String friends = FarmProxy.getFriendsList(userCookie);
      String[] splitFriends = friends.split(",");
      Map<String, String> mapOfFriends = new HashMap<>();
      for (String userFriend : splitFriends) {
        if (userFriend.length() > 1) {
          if (FarmProxy.getMapIDofUserFromDataBase(userFriend) == FarmProxy
              .getMapIDofUserFromDataBase(userCookie)) {
            int[] coordinates = FarmProxy.getRowAndColumnOfUserMapLocation(userFriend);
            mapOfFriends.put(String.valueOf(coordinates[0]) + "," + String.valueOf(coordinates[1]),
                "friend_space");
          }
        }
      }
      Map<String, String> variables = ImmutableMap.of("data", mapdata, "row", row, "col", col,
          "friends", GSON.toJson(mapOfFriends));
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the executing a trade between two players
   *
   */
  private static class MakeTradeHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String tradeData = qm.value("data");
      String[] data = tradeData.split(",");
      String message;
      int cropGiveQ = FarmProxy.getOneInventoryItem(userCookie, data[3]);
      int cropGetQ = FarmProxy.getOneInventoryItem(userCookie, data[1]);
      int sellQ = Integer.parseInt(data[2]);
      int buyQ = Integer.parseInt(data[4]);
      if (cropGiveQ > Integer.parseInt(data[4])) {
        FarmProxy.updateInventory(userCookie, data[1],
                FarmProxy.getOneInventoryItem(userCookie, data[1]) + sellQ);
        FarmProxy.updateInventory(userCookie, data[3],
                FarmProxy.getOneInventoryItem(userCookie, data[3]) - buyQ);
        FarmProxy.updateInventory(data[0], data[1],
            FarmProxy.getOneInventoryItem(userCookie, data[1]) - sellQ);
        FarmProxy.updateInventory(data[0], data[3],
            FarmProxy.getOneInventoryItem(userCookie, data[3]) + buyQ);
        FarmProxy.removeTradeListing(tradeData);
        message = "Trade Successful!";
      } else {
        message = "Not enough " + data[3] + " to make this trade";
      }
      Map<String, String> variables = ImmutableMap.of("message", message);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle when the map is clicked on in the map react component
   * so we can see which map location belongs to which friend.
   *
   */
  private static class ClickingFriendOnMapHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String row = qm.value("row");
      String col = qm.value("col");
      String friendName = FarmProxy.getUserNameFromRowAndColumnOfUserMap(Integer.parseInt(row),
          Integer.parseInt(col), FarmProxy.getMapIDofUserFromDataBase(userCookie));
      Map<String, String> variables = ImmutableMap.of("name", friendName);
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

}
