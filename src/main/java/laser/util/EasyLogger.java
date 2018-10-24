package laser.util;

import java.util.logging.*;

/**
 * Logging Utility
 *
 * @author Marcelo Schmitt Laser
 */
public class EasyLogger
{
  private static Logger LOGGER = null;
  public static final Level LEVEL = Level.FINEST;

  public static boolean initializeLogger(String logFile)
  {
    if(CompilerDirectives.DEBUG)
    {
      EasyLogger.LOGGER = Logger.getLogger(logFile);

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
        EasyLogger.LOGGER.addHandler(handler);
        EasyLogger.LOGGER.setLevel(EasyLogger.LEVEL);
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
      EasyLogger.LOGGER.log(level, msg);
      return true;
    }
    return false;
  }
}
