package laser.cs610.hw4;

import laser.cs610.SootExtension;

public class SymEx
  extends SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  private Integer _evaluationLine;
  private SymExDriver _driver;
  private static String _usageInstructions;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SymEx(String sootClassPathAppend, String className,
    Integer evaluationLine, String methodName)
  {
    super(className, sootClassPathAppend, methodName);
    _evaluationLine = evaluationLine;
  }

  public static SymEx getInstance(String[] args)
  {
    SymEx symEx = unpackArguments(args);
    symEx._driver = new SymExDriver(symEx._evaluationLine, symEx._cfgSoot);
    return symEx;
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String[] args)
  {
    SymEx symEx = getInstance(args);
    System.out.print(symEx._driver.isFeasible());
  }

  private static SymEx unpackArguments(String[] args)
  {
    _usageInstructions = "Usage: java -jar cs610-SymEx.jar <SootClassPath> ";
    _usageInstructions += "<ClassToAnalyzeName> <MethodToAnalyzeName> ";
    _usageInstructions += "<SourceLineToEvaluate>";

    if(args.length < 4)
    {
      System.out.println(_usageInstructions);
      System.exit(1);
    }

    String sootClassPathAppend = args[0];
    String className = args[1];
    String methodName = args[2];
    Integer evaluationLine = Integer.parseInt(args[3]);
    return new SymEx(sootClassPathAppend, className,
      evaluationLine, methodName);
  }
  // </editor-fold> IO OPERATIONS **********************************************

  // <editor-fold> DEBUG *******************************************************
  public boolean isFeasible()
  {
    return _driver.isFeasible();
  }
  // </editor-fold> DEBUG ******************************************************
}
