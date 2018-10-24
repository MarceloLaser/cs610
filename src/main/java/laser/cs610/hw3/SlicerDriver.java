package laser.cs610.hw3;

import java.util.logging.Level;
import laser.util.EasyLogger;
import laser.util.CompilerDirectives;
import soot.jimple.internal.JReturnVoidStmt;
import soot.Unit;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
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
  private Set<Integer> _slice;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SlicerDriver(Integer sliceLine, String sliceVariable,
    ExceptionalUnitGraph sootCfg)
  {
    _sliceLine = sliceLine;
    _sliceVariable = sliceVariable;
    _sootCfg = sootCfg;
    _slice = new HashSet<Integer>();
    buildPdg();
    buildSlice();
  }

  public SlicerDriver(ExceptionalUnitGraph sootCfg)
  {
    _sootCfg = sootCfg;
    _slice = new HashSet<Integer>();
    buildPdg();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public Map<Integer, SootNode> getPdg()
  {
    return new HashMap<Integer, SootNode>(_pdg);
  }
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  private void buildPdg()
  {
    Collection<SootNode> controlDependencies;
    _pdg = new RdduFacade(_sootCfg).nodesMap();
    //removeReturnVoidStmt();
    createCDG();

    for(SootNode node : _pdg.values())
    {
      for(String use : node._dataFlow.getUseSet())
      {
        if(CompilerDirectives.DEBUG)
        {
          String toLog = "Looking at line " + node._lineNumber;
          toLog += " for source of variable " + use;
          toLog += ". Predecessors are: ";
          for(SootNode pred : node._controlFlow.getParents().values())
            toLog += pred._lineNumber + ",";
          EasyLogger.log(Level.FINE, toLog);
        }
        SootNode source = _pdg.get(node._dataFlow.getInSet().get(use));
        if(source.equals(node))
          continue;
        source._controlFlow.addTransition(node._lineNumber, use);
        node._controlFlow.addBackwardTransition(source._lineNumber, use);
      }

      controlDependencies = node._advancedControlFlow.getControlDependencies();
      for(SootNode controlDependency : controlDependencies)
      {
        controlDependency._controlFlow.addTransition
          (node._lineNumber, "Control");
        node._controlFlow.addBackwardTransition
          (controlDependency._lineNumber, "Control");
      }
      if(controlDependencies.isEmpty() && node._lineNumber != -1)
      {
        _pdg.get(-1)._controlFlow.addTransition(node._lineNumber, "Control");
        node._controlFlow.addBackwardTransition(-1, "Control");
      }
    }
  }

  private void buildSlice()
  {
    _slice.add(_sliceLine);
    Queue<SootNode> checkQueue = new LinkedList<SootNode>();
    Set<SootNode> checked = new HashSet<SootNode>();
    checked.add(_pdg.get(_sliceLine));
    for(SootTransition transition :
      _pdg.get(_sliceLine)._controlFlow.getBackwardTransitions())
      if(transition._transitionLabel.equals("Control")
        || transition._transitionLabel.equals(_sliceVariable))
        checkQueue.add(_pdg.get(transition._targetLineNumber));

    while(checkQueue.size() > 0)
    {
      SootNode current = checkQueue.poll();
      if(current._lineNumber != -1)
        _slice.add(current._lineNumber);
      checked.add(current);
      for(SootTransition transition :
        current._controlFlow.getBackwardTransitions())
      {
        SootNode checking = _pdg.get(transition._targetLineNumber);
        if(checked.contains(checking))
          continue;
        checkQueue.add(checking);
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
    EasyFilePrinter.printFile(outputName, pdg);
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
    EasyFilePrinter.printFile(outputName, cd);
  }

  public void printSlice(String outputName)
  {
    String result = "";
    for(Integer sourceLine : _slice)
      result += sourceLine + "\t";
    result += System.lineSeparator();
    EasyFilePrinter.printFile(outputName, result);
  }
  // </editor-fold> DEBUG ******************************************************
}
