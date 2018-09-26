package laser.datastructures.soot;

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

  public void copyTransitions(SootNode node)
  {
    _transitions.addAll(node.getTransitions());
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
    String result = "Line " + _lineNumber + "\t";
    for(Map.Entry<String,Integer> pair : getInSet().entrySet())
    {
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
      result += " " + definitionLine + ", " + use + " -> " + _lineNumber + ";";
      result += System.lineSeparator();
    }

    return result;
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
