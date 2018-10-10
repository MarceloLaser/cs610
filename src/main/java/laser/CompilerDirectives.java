package laser;

import java.util.logging.*;

/**
 * General purpose class used to control compile-time directives and logging.
 *
 * @author Marcelo Schmitt Laser
 * @version 0.3.1a
 */
public class CompilerDirectives
{
  // Assert true for debugging blocks.
  public static final boolean DEBUG = true;
  // Logger
  private static Logger LOGGER = null;
  public static final Level LEVEL = Level.INFO;

  public static boolean initializeLogger(String logFile)
  {
    CompilerDirectives.LOGGER = Logger.getLogger(logFile);

    if(CompilerDirectives.DEBUG)
    {
      try
      {
        java.util.logging.Handler handler = new
          java.util.logging.FileHandler(logFile, Integer.MAX_VALUE, 1);
        handler.setFormatter(new SimpleFormatter()
          {
            private static final String format = "[%1$-7s] %2$s %n";

            @Override
            public synchronized String format(LogRecord lr)
            {
              return String.format(format, lr.getLevel().getLocalizedName(),
                lr.getMessage());
            }
          }
        );
        CompilerDirectives.LOGGER.addHandler(handler);
        CompilerDirectives.LOGGER.setLevel(CompilerDirectives.LEVEL);
        return true;
      }
      catch(Exception e)
      {
        return false;
      }
    }
    return false;
  }

  public static boolean log(Level level, String msg)
  {
    if(CompilerDirectives.DEBUG)
    {
      CompilerDirectives.LOGGER.log(level, msg);
      return true;
    }
    return false;
  }
}
