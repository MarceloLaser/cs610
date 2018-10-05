package laser.cs610.hw1;

import java.util.Collection;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import laser.datastructures.soot.*;
import laser.cs610.*;

public class CFG
  extends SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  private String _outputGraphName;
  private CfgFacade _controlUnit;
  private Collection<SootNode> _nodes;
  private static String _usageInstructions;
  // </editor-fold> FIELDS *****************************************************

  // </editor-fold> INITIALIZATION *********************************************
  public CFG(String className, String outputGraphName,
    String sootClassPathAppend)
  {
    super(className, sootClassPathAppend);
    _outputGraphName = outputGraphName;
    _controlUnit = new CfgFacade();
    _usageInstructions = "Usage: java -jar hw1.jar <ClassToAnalyzeName> ";
    _usageInstructions += "<OutputGraphName> <SootClassPath>";
    _nodes = _controlUnit.compute(_units);
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String[] args)
  {
    CFG cfg = unpackArguments(args);
    cfg.serialize();
  }

  private static CFG unpackArguments(String[] args)
  {
    if(args.length < 3)
    {
      System.out.println(_usageInstructions);
      System.exit(1);
    }

    String className = args[0];
    String outputGraphName = args[1];
    String sootClassPathAppend = args[2];
    return new CFG(className, outputGraphName, sootClassPathAppend);
  }

  public void serialize()
  {
    File file = new File(_outputGraphName);
    PrintWriter writer = null;
    try
    {
      writer = new PrintWriter(file);
    }
    catch(FileNotFoundException e)
    {
      e.printStackTrace();
    }
    writer.println("digraph control_flow_graph {");
    writer.println("	node [shape = rectangle]; entry exit;");
    writer.println("	node [shape = circle];");
    writer.println("");
    for(SootNode node : _nodes)
    {
      for(SootTransition transition : node.getTransitions())
      {
        if(node._lineNumber == transition._targetLineNumber)
          continue;
        String output = "  ";
        if(node._lineNumber == 0)
          output += "entry";
        else
          output += node._lineNumber;
        output += " -> ";
        if(transition._targetLineNumber == -1)
          output += "exit";
        else
          output += transition._targetLineNumber;
        if(transition._transitionLabel != null)
          output += " [label = \"" + transition._transitionLabel + "\"]";
        output += ";";
        writer.println(output);
      }
    }
    writer.println("}");
    writer.close();
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
