package laser.cs610;

import java.util.logging.Level;
import java.io.IOException;
import laser.cs610.hw5.Instrumenter;
import java.util.Collection;
import java.util.Arrays;
import laser.util.EasyLogger;
import soot.G;
import java.io.File;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class InstrumenterTest
{
  private String _subject;
  public InstrumenterTest(String subject)
  {
    super();
    _subject = subject;
  }

  @Parameterized.Parameters
  public static Collection input()
  {
    return Arrays.asList(new Object[][]
      {
        { "InstrumentationTestFileButchered" }
      }
    );
  }

  @Before
  public void initialize()
  {
    G.reset();
  }

  @Test
  public void testInstrumenter()
  {
    new File("target" + File.separator + "InstrumenterTestResults").mkdirs();
    EasyLogger.initializeLogger("target" + File.separator
      + "InstrumenterTestResults" + File.separator + _subject + "TestResults.txt");
    try
    {
      Instrumenter.main(initializeSubject(_subject));
    }
    catch(IOException e)
    {
      EasyLogger.log(Level.SEVERE, "It borked.");
    }
  }

  private static String[] initializeSubject(String subject)
  {
    String className = subject;
    String sootClassPath = System.getProperty("user.dir") + File.separator
      + "src" + File.separator + "test" + File.separator + "resources";
    String outputDirectory = "target" + File.separator
      + "InstrumenterTestResults";
    String methodName = "main";
    return new String[]{sootClassPath, className, outputDirectory, methodName};
  }
}
