package laser.datastructures.soot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SootControlNode
{
  // <editor-fold> FIELDS ******************************************************
  private Set<SootTransition> _transitions;
  private Set<SootTransition> _backwardTransitions;
  private Map<Integer,SootNode> _successors;
  private Map<Integer,SootNode> _parents;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootControlNode()
  {
    _transitions = new HashSet<SootTransition>();
    _backwardTransitions = new HashSet<SootTransition>();
    _successors = new HashMap<Integer,SootNode>();
    _parents = new HashMap<Integer,SootNode>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public void addSuccessor(Integer lineNumber, SootNode successor)
  {
    _successors.put(lineNumber, successor);
  }

  public void addParent(Integer lineNumber, SootNode parent)
  {
    _parents.put(lineNumber, parent);
  }

  public void addTransition(Integer targetLineNumber, String label)
  {
    _transitions.add(new SootTransition(targetLineNumber, label));
  }

  public void addBackwardTransition(Integer targetLineNumber, String label)
  {
    _backwardTransitions.add(new SootTransition(targetLineNumber, label));
  }

  public void addExit(SootNode exit)
  {
    if(_successors.isEmpty())
      _successors.put(exit._lineNumber, exit);
  }

  public void copyTransitions(SootNode node)
  {
    _transitions.addAll(node._controlFlow.getTransitions());
  }

  public Map<Integer,SootNode> getSuccessors()
  {
    return new HashMap<Integer,SootNode>(_successors);
  }

  public Map<Integer,SootNode> getParents()
  {
    return new HashMap<Integer,SootNode>(_parents);
  }

  public Set<SootTransition> getTransitions()
  {
    return new HashSet<SootTransition>(_transitions);
  }

  public Set<SootTransition> getBackwardTransitions()
  {
    return new HashSet<SootTransition>(_backwardTransitions);
  }
  // </editor-fold> ACCESSORS **************************************************
}
