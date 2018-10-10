package laser.cs610;

import java.util.Collection;
import java.util.Arrays;
import laser.util.CompilerDirectives;
import laser.util.EasyLogger;
import laser.cs610.hw1.CFG;
import soot.G;
import java.io.File;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;

@RunWith(Parameterized.class)
public class CFGTest
{
  private String _subject;
  public CFGTest(String subject)
  {
    super();
    _subject = subject;
  }

  @Parameterized.Parameters
  public static Collection input()
  {
    return Arrays.asList(new Object[][]
      {
        { "csci610.cfg.samples.Subject1" },
        { "csci610.cfg.samples.Subject2" },
        { "Example1" },
        { "SwitchInt" },
        { "SwitchString" }
      }
    );
  }

  @Before
  public void initialize()
  {
    G.reset();
  }

  @Test
  public void testCFG()
  {
    new File("target" + File.separator + "CFGTestResults").mkdirs();
    EasyLogger.initializeLogger("target" + File.separator
      + "CFGTestResults" + File.separator + _subject + "TestResults.txt");
    CFG.main(initializeSubject(_subject));
    assert(true);
  }

  private static String[] initializeSubject(String subject)
  {
    String className = subject;
    String outputFileName = "target" + File.separator
      + "CFGTestResults" + File.separator + subject + ".dotty";
    String sootClassPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    return new String[]{className, outputFileName, sootClassPath};
  }
}
