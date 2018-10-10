package laser.datastructures.soot;

import java.util.Collection;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import laser.CompilerDirectives;

public class SootNode
{
  // <editor-fold> FIELDS ******************************************************
  public final int _lineNumber;
  private Set<SootTransition> _transitions;
  private Set<String> _useSet;
  private Set<SootNode> _dominatorSet;
  private Set<SootNode> _postDominatorSet;
  private Set<SootNode> _dominatedSet;
  private Set<SootNode> _postDominatedSet;
  private Set<SootNode> _dominanceFrontier;
  private Set<SootNode> _controlDependencies;
  private Map<String,Integer> _genSet;
  private Map<String,Integer> _inSet;
  private Map<String,Integer> _outSet;
  private Map<Integer,SootNode> _successors;
  private Map<Integer,SootNode> _parents;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootNode(int lineNumber)
  {
    _lineNumber = lineNumber;
    _transitions = new HashSet<SootTransition>();
    _useSet = new HashSet<String>();
    _dominatorSet = new HashSet<SootNode>();
    _postDominatorSet = new HashSet<SootNode>();
    _dominatedSet = new HashSet<SootNode>();
    _postDominatedSet = new HashSet<SootNode>();
    _dominanceFrontier = new HashSet<SootNode>();
    _controlDependencies = new HashSet<SootNode>();
    _genSet = new HashMap<String,Integer>();
    _inSet = new HashMap<String,Integer>();
    _outSet = new HashMap<String,Integer>();
    _successors = new HashMap<Integer,SootNode>();
    _parents = new HashMap<Integer,SootNode>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public void addUse(String use)
  {
    _useSet.add(use);
  }

  public void addGen(String gen)
  {
    _genSet.put(gen,_lineNumber);
  }

  public void addIn(String in, Integer lineNumber)
  {
    _inSet.put(in,lineNumber);
  }

  public void addOut(String out, Integer lineNumber)
  {
    _outSet.put(out,lineNumber);
  }

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

  public void addDominated(SootNode dominated)
  {
    _dominatedSet.add(dominated);
  }

  public void addPostDominated(SootNode postDominated)
  {
    _postDominatedSet.add(postDominated);
  }

  public void addExit(SootNode exit)
  {
    if(_successors.isEmpty())
      _successors.put(exit._lineNumber, exit);
  }

  public void copyTransitions(SootNode node)
  {
    _transitions.addAll(node.getTransitions());
  }

  public void initializeDominators(Collection<SootNode> nodes)
  {
    if(this._lineNumber == -1)
      _dominatorSet.add(this);
    else
      _dominatorSet.addAll(nodes);
  }

  public void initializePostDominators(Collection<SootNode> nodes)
  {
    if(this._lineNumber == -2)
      _postDominatorSet.add(this);
    else
      _postDominatorSet.addAll(nodes);
  }

  public void forwardDominated()
  {
    for(SootNode dominator : _dominatorSet)
      dominator.addDominated(this);
  }

  public void forwardPostDominated()
  {
    for(SootNode postDominator : _postDominatorSet)
      postDominator.addPostDominated(this);
  }

  public Set<String> getUseSet()
  {
    return new HashSet<String>(_useSet);
  }

  public Map<String,Integer> getGenSet()
  {
    return new HashMap<String,Integer>(_genSet);
  }

  public Map<String,Integer> getInSet()
  {
    return new HashMap<String,Integer>(_inSet);
  }

  public Map<String,Integer> getOutSet()
  {
    return new HashMap<String,Integer>(_outSet);
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

  public Set<SootNode> getDominators()
  {
    return new HashSet<SootNode>(_dominatorSet);
  }

  public Set<SootNode> getPostDominators()
  {
    return new HashSet<SootNode>(_postDominatorSet);
  }

  public Set<SootNode> getDominated()
  {
    return new HashSet<SootNode>(_dominatedSet);
  }

  public Set<SootNode> getPostDominated()
  {
    return new HashSet<SootNode>(_postDominatedSet);
  }

  public Set<SootNode> getDominanceFrontier()
  {
    return new HashSet<SootNode>(_dominanceFrontier);
  }

  public Set<SootNode> getControlDependencies()
  {
    return new HashSet<SootNode>(_controlDependencies);
  }
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  public boolean computeSets()
  {
    Map<String,Integer> oldOutSet = getOutSet();

    _inSet = new HashMap<String,Integer>();
    for(SootNode parent : getParents().values())
      _inSet.putAll(parent.getOutSet());

    _outSet = new HashMap<String,Integer>();
    _outSet.putAll(getInSet());
    _outSet = removeKillSet(getOutSet());
    _outSet.putAll(getGenSet());

    if(CompilerDirectives.DEBUG)
    {
      CompilerDirectives.log(Level.FINE, "" + !_outSet.equals(oldOutSet));
    }

    return !_outSet.equals(oldOutSet);
  }

  private Map<String,Integer> removeKillSet(Map<String,Integer> outSet)
  {
    for(String variable : getGenSet().keySet())
    {
      outSet.remove(variable);
    }

    return outSet;
  }

  public boolean computeDominators(Collection<SootNode> nodes)
  {
    if(_lineNumber == -1)
      return false;

    Set<SootNode> resultingNodes = new HashSet<SootNode>(nodes);
    Set<SootNode> oldDominators = new HashSet<SootNode>(_dominatorSet);

    for(SootNode parent : _parents.values())
      resultingNodes.retainAll(parent.getDominators());
    _dominatorSet = new HashSet<SootNode>();
    _dominatorSet.add(this);
    _dominatorSet.addAll(resultingNodes);
    return !_dominatorSet.equals(oldDominators);
  }

  public boolean computePostDominators(Collection<SootNode> nodes)
  {
    if(_lineNumber == -2)
      return false;

    Set<SootNode> resultingNodes = new HashSet<SootNode>(nodes);
    Set<SootNode> oldPostDominators = new HashSet<SootNode>(_postDominatorSet);

    for(SootNode child : _successors.values())
      resultingNodes.retainAll(child.getPostDominators());
    _postDominatorSet = new HashSet<SootNode>();
    _postDominatorSet.add(this);
    _postDominatorSet.addAll(resultingNodes);
    return !_postDominatorSet.equals(oldPostDominators);
  }

  public void computeDominanceFrontiers()
  {
    for(SootNode dominated : _dominatedSet)
      for(SootNode nodeToCheck : dominated.getSuccessors().values())
        if(!_dominatedSet.contains(nodeToCheck))
          _dominanceFrontier.add(nodeToCheck);
  }

  public void computeControlDependencies()
  {
    for(SootNode postDominated : _postDominatedSet)
      for(SootNode nodeToCheck : postDominated.getParents().values())
        if(!_postDominatedSet.contains(nodeToCheck))
          _controlDependencies.add(nodeToCheck);
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public String printNode()
  {
    String result = "Node #: " + _lineNumber + System.lineSeparator();
    for(String definition : getGenSet().keySet())
      result += "Definition: " + definition + System.lineSeparator();
    for(String usage : _useSet)
      result += "Use: " + usage + System.lineSeparator();
    for(Integer parent : getParents().keySet())
      result += "Parent: " + parent + System.lineSeparator();
    for(Integer successor : getSuccessors().keySet())
      result += "Successor: " + successor + System.lineSeparator();
    result += System.lineSeparator();
    return result;
  }

  public String printReachingDefinitions()
  {
    String result;
    if(_lineNumber == -1)
      result = "Line entry\t";
    else
      result = "Line " + _lineNumber + "\t";
    for(Map.Entry<String,Integer> pair : getInSet().entrySet())
    {
      if(pair.getValue() == -1)
        result += "<" + pair.getKey() + ", entry>\t";
      else
        result += "<" + pair.getKey() + ", " + pair.getValue() + ">\t";
    }
    result += System.lineSeparator();
    return result;
  }

  public String printDU()
  {
    String result = "";

    for(String use : getUseSet())
    {
      int definitionLine = getInSet().get(use);
      if(definitionLine == -1)
        result += " entry, " + use + " -> ";
      else
        result += " " + definitionLine + ", " + use + " -> ";
      if(_lineNumber == -1)
        result += "entry;";
      else
        result += _lineNumber + ";";
      result += System.lineSeparator();
    }

    return result;
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
