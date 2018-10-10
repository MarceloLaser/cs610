package laser.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;

/**
 * Simple String file printing Utility
 *
 * @author Marcelo Schmitt Laser
 */
public class EasyFilePrinter
{
  public static void PrintFile(String path, String fileName, String content)
  {
    PrintFile(path + File.separator + fileName, content);
  }

  public static void PrintFile(String fullName, String content)
  {
    File outputFile = new File(fullName);
    PrintWriter writer = initializeWriter(outputFile);
    writer.write(content);
    writer.close();
  }

  private static PrintWriter initializeWriter(File file)
  {
    PrintWriter writer = null;
    try
    {
      writer = new PrintWriter(file);
    }
    catch(FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return writer;
  }
}
