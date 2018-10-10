package laser.cs610;

import laser.cs610.hw3.Slicer;
import java.util.Collection;
import java.util.Arrays;
import laser.CompilerDirectives;
import soot.G;
import java.io.File;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class SlicerTest
{
  private String _subject;
  public SlicerTest(String subject)
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
    new File("target" + File.separator + "SlicerTestResults").mkdirs();
    CompilerDirectives.initializeLogger("target" + File.separator
      + "SlicerTestResults" + File.separator + _subject + "TestResults.txt");
    Slicer.main(initializeSubject(_subject));
    assert(true);
  }

  private static String[] initializeSubject(String subject)
  {
    String className = subject;
    String outputName = "target" + File.separator
      + "SlicerTestResults" + File.separator + subject + "Slice.txt";
    String sootClassPath = System.getProperty("user.dir") + File.separator
      + "src" + File.separator + "test" + File.separator + "resources";
    String sourceSliceLine = "5";
    String sourceSliceVariable = "v";
    String pdgFileName = "target" + File.separator
      + "SlicerTestResults" + File.separator + subject + "PDG.dotty";
    return new String[]{sootClassPath, className, outputName,
      sourceSliceLine, sourceSliceVariable, pdgFileName};
  }
}
