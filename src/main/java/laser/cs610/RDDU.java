package laser.cs610;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.util.logging.Level;
import laser.CompilerDirectives;
import laser.datastructures.soot.*;

public class RDDU extends SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  public final RdduFacade _controlUnit;
  private String _outputDUFile;
  private String _outputRDFile;
  private static String _usageInstructions;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public RDDU(String className, String duFileName,
    String rdFileName, String sootClassPathAppend)
  {
    super(className, sootClassPathAppend);
    _outputDUFile = duFileName;
    _outputRDFile = rdFileName;
    _controlUnit = new RdduFacade(_cfgSoot);
    _usageInstructions = "Usage: java -jar hw2.jar <ClassToAnalyzeName> ";
    _usageInstructions += "<OutputDUName> <OutputRDName> <SootClassPath>";
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String[] args)
  {
    RDDU rddu = unpackArguments(args);

    if(CompilerDirectives.DEBUG)
      CompilerDirectives.log(Level.INFO, rddu._controlUnit.printNodes());

    rddu.serialize();
  }

  private static RDDU unpackArguments(String[] args)
  {
    if(args.length < 4)
    {
      System.out.println(_usageInstructions);
      System.exit(1);
    }

    String className = args[0];
    String duFileName = args[1];
    String rdFileName = args[2];
    String sootClassPathAppend = args[3];
    return new RDDU(className, duFileName, rdFileName, sootClassPathAppend);
  }

  public void serialize()
  {
    serializeDU();
    serializeRD();
  }

  private void serializeRD()
  {
    File rdFile = new File(_outputRDFile);

    PrintWriter writer = initializeWriter(rdFile);

    for(SootNode node : _controlUnit.allNodes())
      writer.write(node.printReachingDefinitions());
    writer.close();
  }

  private void serializeDU()
  {
    File duFile = new File(_outputDUFile);

    PrintWriter writer = initializeWriter(duFile);

    writer.write("digraph definition_use_graph {" + System.lineSeparator());
    writer.write(" node [shape = rectangle];" + System.lineSeparator());
    writer.write(System.lineSeparator());
    for(SootNode node : _controlUnit.allNodes())
      writer.write(node.printDU());
    writer.write("}");

    writer.close();
  }

  private PrintWriter initializeWriter(File file)
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
  // </editor-fold> IO OPERATIONS **********************************************
}
