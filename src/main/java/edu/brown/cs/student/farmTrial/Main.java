package edu.brown.cs.student.farmTrial;

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
    FarmTrialApp app = new FarmTrialApp(repl);
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
    Spark.post("/new_user", new NewUserPageHandler(), freeMarker);
    Spark.get("/logout", new LogOutHandler(), freeMarker);
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
      // checking to make sure the user isn't already logged on.
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
      // Making sure that the username they are trying to make doesn't exist already.
      if (FarmProxy.getUserNameFromDataBase(username) != null) {
        createMessage = "The username already exists. Please try a different username.";
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
          Arrays.toString(salt));
      userCookie = username;
      res.cookie(username, username);
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator");
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
      Map<String, Object> variables = ImmutableMap.of("title", "Farming Simulator", "message",
          message);
      if (req.cookies().containsKey(userCookie)) {
        System.out.println("dfgdfgdgd");
        res.removeCookie(userCookie);
      }
      System.out.println(req.cookies().size());
      message = "You have been logged out. Thank you.";
      return new ModelAndView(variables, "home.ftl");
    }
  }
}
