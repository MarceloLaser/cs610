package laser.cs610.hw3;

import java.util.Iterator;
import soot.jimple.internal.JReturnVoidStmt;
import soot.Unit;
import java.util.Collection;
import java.util.Map;
import soot.toolkits.graph.ExceptionalUnitGraph;
import laser.datastructures.soot.SootNode;
import laser.datastructures.soot.SootTransition;
import laser.cs610.hw2.RdduFacade;
import laser.util.EasyFilePrinter;

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
    Collection<SootNode> controlDependencies;
    _pdg = new RdduFacade(_sootCfg).nodesMap();
    removeReturnVoidStmt();
    createCDG();

    for(SootNode node : _pdg.values())
    {
      for(String use : node._dataFlow.getUseSet())
      {
        SootNode source = _pdg.get(node._dataFlow.getInSet().get(use));
        if(source.equals(node))
          continue;
        source._controlFlow.addTransition(node._lineNumber, use);
      }

      controlDependencies = node._advancedControlFlow.getControlDependencies();
      for(SootNode controlDependency : controlDependencies)
      {
        controlDependency._controlFlow.addTransition
          (node._lineNumber, "Control");
      }
      if(controlDependencies.isEmpty() && node._lineNumber != -1)
      {
        _pdg.get(-1)._controlFlow.addTransition(node._lineNumber, "Control");
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
      node._controlFlow.addExit(exit);
      node._advancedControlFlow.initializePostDominators(nodes, node);
    }

    while(changes)
    {
      changes = false;
      for(SootNode node : nodes)
        changes = changes ||
          node._advancedControlFlow.computePostDominators(nodes, node);
    }

    for(SootNode node : nodes)
      node._advancedControlFlow.forwardPostDominated(node);

    for(SootNode node : nodes)
      node._advancedControlFlow.computeControlDependencies();
  }

  private void removeReturnVoidStmt()
  {
    Iterator<Unit> it = _sootCfg.iterator();
    while(it.hasNext())
    {
      Unit u = it.next();
      if(u instanceof JReturnVoidStmt)
        _pdg.remove(u.getJavaSourceStartLineNumber());
    }
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> DEBUG *******************************************************
  public void printPDG(String outputName)
  {
    String pdg = "";
    pdg += "digraph pdg {" + System.lineSeparator();
    pdg += " node [shape = rectangle];" + System.lineSeparator();
    pdg += System.lineSeparator();

    for(SootNode node : _pdg.values())
      for(SootTransition transition : node._controlFlow.getTransitions())
        pdg += " " + node._lineNumber + ", " + transition._transitionLabel +
          " -> " + transition._targetLineNumber + ";" + System.lineSeparator();

    pdg += "}" + System.lineSeparator();
    EasyFilePrinter.PrintFile(outputName, pdg);
  }

  public void printControlDependencies(String outputName)
  {
    Collection<SootNode> dependencies;
    String cd = "";
    cd += "digraph pdg {" + System.lineSeparator();
    cd += " node [shape = rectangle];" + System.lineSeparator();
    cd += System.lineSeparator();

    for(SootNode node : _pdg.values())
    {
      dependencies = node._advancedControlFlow.getControlDependencies();
      for(SootNode dependency : dependencies)
        cd += " " + node._lineNumber + " -> "
          + dependency._lineNumber + ";" + System.lineSeparator();
    }

    cd += "}" + System.lineSeparator();
    EasyFilePrinter.PrintFile(outputName, cd);
  }
  // </editor-fold> DEBUG ******************************************************
}
