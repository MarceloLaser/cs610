package laser.datastructures.soot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SootCallgraphControlNode
{
  // <editor-fold> FIELDS ******************************************************
  private Set<SootTransition> _transitions;
  private Set<SootTransition> _backwardTransitions;
  private Map<String,SootNode> _successors;
  private Map<String,SootNode> _parents;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootCallgraphControlNode()
  {
    _transitions = new HashSet<SootTransition>();
    _backwardTransitions = new HashSet<SootTransition>();
    _successors = new HashMap<String,SootNode>();
    _parents = new HashMap<String,SootNode>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public void addSuccessor(String lineNumber, SootNode successor)
  {
    _successors.put(lineNumber, successor);
  }

  public void removeSuccessor(String lineNumber)
  {
    _successors.remove(lineNumber);
  }

  public void addParent(String lineNumber, SootNode parent)
  {
    _parents.put(lineNumber, parent);
  }

  public void removeParent(String lineNumber)
  {
    _parents.remove(lineNumber);
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
      _successors.put(exit._name, exit);
  }

  public void copyTransitions(SootNode node)
  {
    _transitions.addAll(node._controlFlow.getTransitions());
  }

  public Map<String,SootNode> getSuccessors()
  {
    return new HashMap<String,SootNode>(_successors);
  }

  public Map<String,SootNode> getParents()
  {
    return new HashMap<String,SootNode>(_parents);
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
