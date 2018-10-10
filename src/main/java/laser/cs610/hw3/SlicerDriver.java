package laser.cs610.hw3;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import soot.toolkits.graph.ExceptionalUnitGraph;
import laser.datastructures.soot.SootNode;
import laser.datastructures.soot.SootTransition;
import laser.cs610.hw2.RdduFacade;

public class SlicerDriver
{
  // <editor-fold> FIELDS ******************************************************
  private Integer _sliceLine;
  private String _sliceVariable;
  private ExceptionalUnitGraph _sootCfg;
  private Map<Integer, SootNode> _pdg;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SlicerDriver(Integer sliceLine, String sliceVariable,
    ExceptionalUnitGraph sootCfg)
  {
    _sliceLine = sliceLine;
    _sliceVariable = sliceVariable;
    _sootCfg = sootCfg;
    buildPdg();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  private void buildPdg()
  {
    _pdg = new RdduFacade(_sootCfg).nodesMap();
    createCDG();

    for(SootNode node : _pdg.values())
    {
      for(String use : node.getUseSet())
      {
        SootNode source = _pdg.get(node.getInSet().get(use));
        source.addTransition(node._lineNumber, null);
      }

      for(SootNode controlDependency : node.getControlDependencies())
      {
        controlDependency.addTransition(node._lineNumber, null);
      }
    }
  }

  private void createCDG()
  {
    Collection<SootNode> nodes = _pdg.values();
    boolean changes = true;
    SootNode exit = new SootNode(-2);

    for(SootNode node : nodes)
    {
      node.addExit(exit);
      node.initializePostDominators(nodes);
    }

    while(changes)
    {
      changes = false;
      for(SootNode node : nodes)
        changes = changes || node.computePostDominators(nodes);
    }

    for(SootNode node : nodes)
      node.forwardPostDominated();

    for(SootNode node : nodes)
      node.computeControlDependencies();
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> DEBUG *******************************************************
  public void printPDG(String outputName)
  {
    File pdgFile = new File(outputName);
    PrintWriter writer = initializeWriter(pdgFile);
    String pdg = "";

    pdg += "digraph pdg {" + System.lineSeparator();
    pdg += " node [shape = rectangle];" + System.lineSeparator();
    pdg += System.lineSeparator();

    for(SootNode node : _pdg.values())
      for(SootTransition transition : node.getTransitions())
        pdg += " " + node._lineNumber + " -> "
          + transition._targetLineNumber + ";" + System.lineSeparator();

    pdg += "}" + System.lineSeparator();

    writer.write(pdg);
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
  // </editor-fold> DEBUG ******************************************************
}
