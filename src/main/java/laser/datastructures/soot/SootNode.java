package laser.datastructures.soot;

import java.util.Collection;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import laser.util.CompilerDirectives;

public class SootNode
{
  // <editor-fold> FIELDS ******************************************************
  public final int _lineNumber;
  public final SootAdvancedControlNode _advancedControlFlow;
  public final SootControlNode _controlFlow;
  public final SootDataNode _dataFlow;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootNode(int lineNumber)
  {
    _lineNumber = lineNumber;
    _advancedControlFlow = new SootAdvancedControlNode();
    _controlFlow = new SootControlNode();
    _dataFlow = new SootDataNode();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  public boolean computeSets()
  {
    return _dataFlow.computeSets(_controlFlow.getParents().values());
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public String printNode()
  {
    Collection<String> definitions = _dataFlow.getGenSet().keySet();
    Collection<String> uses = _dataFlow.getUseSet();
    Collection<Integer> parents = _controlFlow.getParents().keySet();
    Collection<Integer> successors = _controlFlow.getSuccessors().keySet();
    String result = "Node #: " + _lineNumber + System.lineSeparator();

    for(String definition : definitions)
      result += "Definition: " + definition + System.lineSeparator();
    for(String usage : uses)
      result += "Use: " + usage + System.lineSeparator();
    for(Integer parent : parents)
      result += "Parent: " + parent + System.lineSeparator();
    for(Integer successor : successors)
      result += "Successor: " + successor + System.lineSeparator();
    result += System.lineSeparator();
    return result;
  }

  public String printReachingDefinitions()
  {
    return _dataFlow.printReachingDefinitions(_lineNumber);
  }

  public String printDU()
  {
    return _dataFlow.printDU(_lineNumber);
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
