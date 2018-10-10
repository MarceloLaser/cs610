package laser.cs610.hw3;

import laser.util.CompilerDirectives;
import laser.cs610.SootExtension;

public class Slicer
  extends SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  private String _outputFile;
  private String _sliceVariable;
  private Integer _sliceLine;
  private static String _usageInstructions;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public Slicer(String sootClassPathAppend, String className, String outputPath,
    Integer sliceLine, String sliceVariable)
  {
    super(className, sootClassPathAppend);
    _sliceLine = sliceLine;
    _sliceVariable = sliceVariable;
    _outputFile = outputPath;
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String[] args)
  {
    Slicer slicer = unpackArguments(args);
    SlicerDriver driver = new SlicerDriver(slicer._sliceLine,
      slicer._sliceVariable, slicer._cfgSoot);

    if(CompilerDirectives.DEBUG)
    {
      driver.printPDG(args[5]);
      driver.printControlDependencies(args[6]);
    }
  }

  private static Slicer unpackArguments(String[] args)
  {
    _usageInstructions = "Usage: java -jar hw3.jar <SootClassPath> ";
    _usageInstructions += "<ClassToAnalyzeName> <OutputName> ";
    _usageInstructions += "<SourceSliceLine> <SourceSliceVariable>";

    if(args.length < 5)
    {
      System.out.println(_usageInstructions);
      System.exit(1);
    }

    String sootClassPathAppend = args[0];
    String className = args[1];
    String outputPath = args[2];
    Integer sliceLine = Integer.parseInt(args[3]);
    String sliceVariable = args[4];
    return new Slicer(sootClassPathAppend, className, outputPath,
      sliceLine, sliceVariable);
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
