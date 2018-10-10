package laser.cs610;

import java.util.Collection;
import java.util.Arrays;
import laser.util.CompilerDirectives;
import laser.util.EasyLogger;
import laser.cs610.hw2.RDDU;
import soot.G;
import java.io.File;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class RDDUTest
{
  private String _subject;
  public RDDUTest(String subject)
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
  public void testDataFlow()
  {
    new File("target" + File.separator + "DataFlowTestResults").mkdirs();
    EasyLogger.initializeLogger("target" + File.separator
      + "DataFlowTestResults" + File.separator + _subject + "TestResults.txt");
    RDDU.main(initializeSubject(_subject));
    assert(true);
  }

  private static String[] initializeSubject(String subject)
  {
    String className = subject;
    String outputDUName = "target" + File.separator
      + "DataFlowTestResults" + File.separator + subject + "DU.dotty";
    String outputRDName = "target" + File.separator
      + "DataFlowTestResults" + File.separator + subject + "RD.txt";
    String sootClassPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    return new String[]{className, outputDUName, outputRDName, sootClassPath};
  }
}
