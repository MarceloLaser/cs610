package laser.cs610.hw3;

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
    createCDG();

    for(SootNode node : _pdg.values())
    {
      for(String use : node._dataFlow.getUseSet())
      {
        SootNode source = _pdg.get(node._dataFlow.getInSet().get(use));
        source._controlFlow.addTransition(node._lineNumber, null);
      }

      controlDependencies = node._advancedControlFlow.getControlDependencies();
      for(SootNode controlDependency : controlDependencies)
      {
        controlDependency._controlFlow.addTransition(node._lineNumber, null);
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
        pdg += " " + node._lineNumber + " -> "
          + transition._targetLineNumber + ";" + System.lineSeparator();

    pdg += "}" + System.lineSeparator();
    EasyFilePrinter.PrintFile(outputName, pdg);
  }
  // </editor-fold> DEBUG ******************************************************
}
