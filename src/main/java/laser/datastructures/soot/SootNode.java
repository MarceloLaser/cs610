package laser.datastructures.soot;

import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import com.microsoft.z3.BoolExpr;

public class SootNode
{
  // <editor-fold> FIELDS ******************************************************
  public final int _lineNumber;
  public final SootAdvancedControlNode _advancedControlFlow;
  public final SootControlNode _controlFlow;
  public final SootDataNode _dataFlow;
  private List<BoolExpr> _pathCondition;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootNode(int lineNumber)
  {
    _lineNumber = lineNumber;
    _advancedControlFlow = new SootAdvancedControlNode();
    _controlFlow = new SootControlNode();
    _dataFlow = new SootDataNode();
    _pathCondition = new LinkedList<BoolExpr>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public List<BoolExpr> getPathCondition()
  {
    return _pathCondition;
  }

  public void addPathCondition(BoolExpr pathCondition)
  {
    _pathCondition.add(pathCondition);
  }
  // </editor-fold> ACCESSORS **************************************************

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
