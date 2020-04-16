package edu.brown.cs.jzhang30.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * REPL class manages a set of command names and corresponding
 * commands with executable methods. Allows for the creation of a REPL.
 * @author Daniel
 */
public class REPL {
  private InputStream inStream;
  private HashMap<String, Command> commands;

  /**
   * Create a new REPL instance, with a default .help command
   * registered.
   * @param is input stream to read from
   */
  public REPL(InputStream is) {
    inStream = is;
    commands = new HashMap<String, Command>();
    register(".help", new Command() {
      @Override
      public void execute(String[] tokens, PrintWriter pw) {
        pw.println("Current running REPL");
        pw.println("Usage: command <args>");
        pw.println("Registered commands:");
        Set<String> commandNames = commands.keySet();
        for (String cn : commandNames) {
          pw.println(cn);
        }
        pw.println();
      }
    });
  }

  /**
   * Runs the REPL.
   */
  public void run() {
    // Create a buffered reader for System.in
    try {
      BufferedReader replReader = new BufferedReader(new InputStreamReader(inStream));
      PrintWriter replWriter = new PrintWriter(System.out, true);
      // replWriter.println("REPL initialized with stars commands.");
      String input;
      while ((input = replReader.readLine()) != null) {
        process(input, replWriter);
      }
      replWriter.close();
      replReader.close();
    } catch (IOException e) {
      System.out.println("ERROR: Error occurred during I/O for REPL.");
    }
  }

  /**
   * Register a new command with the CommandManager. When the name is passed to
   * process, runs the command associated with the name. <br>
   * Overwrites previously associated commands with the same name.
   * @param name the name of the command, or the characters before the first space
   *             in the input.
   * @param s    the command to execute
   * @throws IllegalArgumentException if the command name contains a space
   */
  public void register(String name, Command s) {
    if (name.contains(" ")) {
      // Invalid command name
      throw new IllegalArgumentException("Invalid command name.");
    }
    commands.put(name, s);
  }

  /**
   * Processes a line of input, and calls the execute method on any matching
   * commands.
   * @param line the string of input
   * @param pw   the output writer to print results
   */
  public void process(String line, PrintWriter pw) {
    String[] lineParts = line.trim().split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    String commandName = lineParts[0];
    List<String> tokens = new ArrayList<String>();
    for (int i = 1; i < lineParts.length; i++) {
      tokens.add(lineParts[i]);
    }
    Command toRun = commands.get(commandName);
    if (toRun == null) {
      // Command not registered / not correct
      if (!commandName.isEmpty()) {
        pw.println("ERROR: Command not found.");
      }
    } else {
      toRun.execute(tokens.toArray(new String[0]), pw);
    }
  }
}
