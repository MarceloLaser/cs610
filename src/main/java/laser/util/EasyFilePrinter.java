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
  public static void printFile(String path, String fileName, String content)
  {
    printFile(path + File.separator + fileName, content);
  }

  public static void printFile(String fullName, String content)
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
