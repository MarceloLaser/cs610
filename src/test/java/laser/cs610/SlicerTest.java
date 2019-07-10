package laser.cs610;

import laser.cs610.hw3.Slicer;
import java.util.Collection;
import java.util.Arrays;
import laser.util.EasyLogger;
import soot.G;
import java.io.File;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class SlicerTest
{
  private String _subject;
  private String _sliceLine;
  private String _sliceVar;
  public SlicerTest(String subject, String sliceLine, String sliceVar)
  {
    super();
    _subject = subject;
    _sliceLine = sliceLine;
    _sliceVar = sliceVar;
  }

  @Parameterized.Parameters
  public static Collection input()
  {
    return Arrays.asList(new Object[][]
      {
        { "hw.slicing.Program1", "40", "tax" },
        //{ "hw.slicing.Program1", "31", "cars" },
        { "hw.slicing.Program2", "40", "b" },
        //{ "hw.slicing.Program2", "27", "j" },
        { "Program1", "40", "tax" },
        //{ "Program1", "31", "cars" },
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
    EasyLogger.initializeLogger("target" + File.separator
      + "SlicerTestResults" + File.separator + _subject + "TestResults.txt");
    Slicer.main(initializeSubject(_subject, _sliceLine, _sliceVar));
    assert(true);
  }

  private static String[] initializeSubject(String subject, String sliceLine,
    String sliceVar)
  {
    String className = subject;
    String outputName = "target" + File.separator
      + "SlicerTestResults" + File.separator + subject + "Slice.txt";
    String sootClassPath = System.getProperty("user.dir") + File.separator
      + "src" + File.separator + "test" + File.separator + "resources-slicer";
    String sourceSliceLine = sliceLine;
    String sourceSliceVariable = sliceVar;
    String pdgFileName = "target" + File.separator
      + "SlicerTestResults" + File.separator + subject + "PDG.dotty";
    String cdFileName = "target" + File.separator
      + "SlicerTestResults" + File.separator + subject + "CD.dotty";
    return new String[]{sootClassPath, className, outputName,
      sourceSliceLine, sourceSliceVariable, pdgFileName, cdFileName};
  }
}
