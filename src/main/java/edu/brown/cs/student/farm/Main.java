package edu.brown.cs.student.farm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.student.guihandlers.FarmingHandlers;
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
  private REPL repl;
  private static final Gson GSON = new Gson();
  private FarmViewer app;
  private FarmingHandlers farmingHandlers;

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

    // init app
    app = new FarmViewer(repl, "myFarm");
    // init farming handlers
    farmingHandlers = new FarmingHandlers(app);

    FarmProxy.setUpDataBase();
    // Stars the GUI server
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

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

  private void runSparkServer(int port) {
    Spark.port(port);
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
    Spark.post("/new_user", new NewUserPageHandler(), freeMarker);
    Spark.get("/logout", new LogOutHandler(), freeMarker);
    Spark.post("/adding_friend", new AddingFriendsHandler());
    Spark.post("/friendLoader", new FriendLoaderHandler());
    Spark.post("/friendPendingLoader", new FriendPendingLoaderHandler());
    Spark.post("/friendAccepted", new FriendAcceptedHandler());
    Spark.post("/farmland", farmingHandlers.new FarmingHandler());
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

  static String message = "";
  static String createMessage = "";
  static String userCookie = null;

  /**
   * Handle requests to the home page of the farm simulator where the user will
   * login. This page will contain the form for the log in.
   *
   */
  private static class LoginPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator", "message",
          message);
      message = "";
      // checking to make sure the user isn't already logged on.Will take to their
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
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator");
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
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator",
          "create_message", createMessage);
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // check if the hash from the database matches this hash
      if (Arrays.toString(hashedPassword).equals(hashedPasswordFromDataBase) == false) {
        message = "The password is incorrect. Please try again";
        res.redirect("/login");
      }
      userCookie = username;
      res.cookie(username, username);
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator");
      return new ModelAndView(variables, "user_home.ftl");
    }
  }

  /**
   * This is the handler for interpreting the form for creating a new user. The
   * post request will be from the creation of a new user page.It will redirect to
   * the create account page if the data for the creation of the user is invalid.
   * It will load the new user's profile page on acceptance.
   *
   */
  private static class NewUserPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap data = req.queryMap();
      String email = data.value("email");
      String username = data.value("username");
      String password = data.value("password");
      String reEntered = data.value("re_password");

      // making sure the string is of the right format.
      if (!Pattern.matches("[A-Za-z][A-Za-z0-9]{5,16}", username)) {
        createMessage = "The username must start with a letter and be at least 6 characters and at most 16. Try Again";
        res.redirect("/create_account");
        return new ModelAndView(null, "new_user.ftl");
      }
      // making sure the two passwords equal each other.
      if (!password.equals(reEntered)) {
        createMessage = "The passwords didn't match. Try Again";
        res.redirect("/create_account");
        return new ModelAndView(null, "new_user.ftl");
      }
      // Making sure that the user name they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(username) != null) {
        createMessage = "The username already exists. Please try a different username.";
        res.redirect("/create_account");
        return new ModelAndView(null, "new_user.ftl");
      }
      // Making sure that the email they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(email) != null) {
        createMessage = "The email already exists. Please try a different email.";
        res.redirect("/create_account");
        return new ModelAndView(null, "new_user.ftl");
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // insert this user information into the database.
      FarmProxy.insertUserInfoIntoDatabase(username, Arrays.toString(hashedPassword),
          Arrays.toString(salt), email);
      userCookie = username;
      res.cookie(username, username);
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator", "name",
          username);
      return new ModelAndView(variables, "new_user.ftl");
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
      // HAVE TO DO THE CHECK WHERE THE USER ISN'T NEW USER ANYMORE.
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator", "name",
          userCookie);
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
      if (req.cookies().containsKey(userCookie)) {
        System.out.println("dfgdfgdgd");
        res.removeCookie(userCookie);
      }
      System.out.println(req.cookies().size());
      message = "You have been logged out. Thank you.";
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator", "message",
          message);
      return new ModelAndView(variables, "home.ftl");
    }
  }

  /**
   * This class will handle the request for adding a friend and will send a
   * message back to the javascript post request pertaining to the status of
   * adding this friend.
   *
   * @return GSON which contains the result of autocorrect.suggest()
   */
  private static class AddingFriendsHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      // TODO: query the value of the input you want to generate suggestions for
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("text");
      String message = "";
      Map<String, String> variables;
      if (username.equals(userCookie)) {
        message = "You can't add yourself. Try again.";
        variables = ImmutableMap.of("message", message);
        GSON.toJson(variables);
        return GSON.toJson(variables);
      }
      // Making sure that the user name they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(username) == null) {
        System.out.println("doesnt exist");
        message = "The user doesn't exist. Try adding someone else.";
      } else {
        // ADD THE OPTION OF ACCEPTING OR DECLINING FRIENDS
        System.out.println(userCookie);
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
        // check to make sure the user isn't already in the friends list.
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
        FarmProxy.UpdateFriendsPending(userCookie, username);
//        FarmProxy.UpdateFriendsList(userCookie, username);
//        FarmProxy.UpdateFriendsList(username, userCookie);
        message = "sending the request right now";
        System.out.println("adding them");
      }
      // TODO: create an immutable map using the suggestions
      variables = ImmutableMap.of("message", message);
      // TODO: return a Json of the suggestions (HINT: use the GSON instance)
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for displaying the friend's list of a user
   * when they want to see it.
   *
   * @return GSON which contains the result of autocorrect.suggest()
   */
  private static class FriendLoaderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      // TODO: query the value of the input you want to generate suggestions for
      QueryParamsMap qm = req.queryMap();
      String friendslist = FarmProxy.getFriendsList(userCookie);
      // TODO: create an immutable map using the suggestions
      Map<String, String> variables = ImmutableMap.of("list", friendslist);
      // TODO: return a Json of the suggestions (HINT: use the GSON instance)
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for displaying the pending requests that
   * they can accept of a user when they want to see it.
   *
   * @return GSON which contains the friends list pending requests that will be
   *         displayed.
   */
  private static class FriendPendingLoaderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      // TODO: query the value of the input you want to generate suggestions for
      QueryParamsMap qm = req.queryMap();
      String friendslist = FarmProxy.getFriendsListPending(userCookie);
      // TODO: create an immutable map using the suggestions
      Map<String, String> variables = ImmutableMap.of("list", friendslist);
      // TODO: return a Json of the suggestions (HINT: use the GSON instance)
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

  /**
   * This class will handle the request for accepting a friend when it is clicked
   * on from the pending requests and updating the friend's lis of eahc user as
   * well the pending friends list of the current user.
   *
   * @return GSON which contains the name of the friend that is being accepted.
   */
  private static class FriendAcceptedHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      // TODO: query the value of the input you want to generate suggestions for
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("text");
      String friendslistpending = FarmProxy.getFriendsListPending(userCookie);
      friendslistpending = friendslistpending.replace(username + ",", "");
      FarmProxy.UpdateFriendsPendingAfterAdding(friendslistpending, userCookie);
      FarmProxy.UpdateFriendsList(userCookie, username);
      FarmProxy.UpdateFriendsList(username, userCookie);
      // TODO: create an immutable map using the suggestions
      Map<String, String> variables = ImmutableMap.of("list", username);
      // TODO: return a Json of the suggestions (HINT: use the GSON instance)
      GSON.toJson(variables);
      return GSON.toJson(variables);
    }
  }

}
