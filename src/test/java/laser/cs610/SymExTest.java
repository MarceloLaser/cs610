package laser.cs610;

import laser.cs610.hw4.SymEx;
import java.util.Collection;
import java.util.Arrays;
import laser.util.EasyLogger;
import soot.G;
import java.io.File;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class SymExTest
{
  private String _subject;
  private String _lineNumber;
  private boolean _expectedResult;
  private String _subjectMethod;
  public SymExTest(String subject, String lineNumber,
    boolean expectedResult, String methodName)
  {
    super();
    _subject = subject;
    _lineNumber = lineNumber;
    _expectedResult = expectedResult;
    _subjectMethod = methodName;
  }

  @Parameterized.Parameters
  public static Collection input()
  {
    return Arrays.asList(new Object[][]
      {
        { "csci610.cfg.samples.Subject1", "10", true, "main" },
        //{ "csci610.cfg.samples.Subject2", "20", true, "main" },
        //{ "Example1" },
        //{ "SwitchInt", "75" },
        //{ "SwitchString" },
        { "SliceTest", "8", true, "main" },
        { "SymExTest", "14", false, "method1" },
        { "SymExTest", "23", false, "method1" },
        { "SymExTest", "10", true, "method1" },
        { "SymExTest", "8", true, "method1" },
        { "SymExTest", "21", true, "method1" },
        { "SymExTest", "33", true, "method2" },
        { "SymExTest", "56", false, "method3" },
      }
    );
  }

  @Before
  public void initialize()
  {
    G.reset();
  }

  @Test
  public void testSymbolicExecution()
  {
    new File("target" + File.separator + "SymExTestResults").mkdirs();
    EasyLogger.initializeLogger("target" + File.separator
      + "SymExTestResults" + File.separator + _subject + "TestResults.txt");
    SymEx symEx = SymEx.getInstance(
      initializeSubject(_subject, _lineNumber, _subjectMethod));
    assert(symEx.isFeasible() == _expectedResult);
  }

  private static String[] initializeSubject(String subject,
    String lineNumber, String subjectMethod)
  {
    String className = subject;
    String sootClassPath = System.getProperty("user.dir") + File.separator
      + "src" + File.separator + "test" + File.separator + "resources";
    String methodName = subjectMethod;
    String sourceEvalLine = lineNumber;
    return new String[]{sootClassPath, className, methodName, sourceEvalLine};
  }
}
