package edu.brown.cs.jzhang30.repl;

import java.io.PrintWriter;

/**
 * A Command must have an execute method that takes in tokens as arguments and
 * output using a PrintWriter.
 * @author Tim Nelson
 */
public interface Command {
  /**
   * Given a set of arguments, execute a certain action and print the results
   * using pw.
   * @param tokens a list of strings passed as arguments to the command
   * @param pw the output location
   */
  void execute(String[] tokens, PrintWriter pw);
}
