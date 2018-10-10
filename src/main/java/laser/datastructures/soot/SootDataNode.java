package laser.datastructures.soot;

import java.util.logging.Level;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import laser.util.CompilerDirectives;
import laser.util.EasyLogger;

public class SootDataNode
{
  // <editor-fold> FIELDS ******************************************************
  private Set<String> _useSet;
  private Map<String,Integer> _genSet;
  private Map<String,Integer> _inSet;
  private Map<String,Integer> _outSet;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootDataNode()
  {
    _useSet = new HashSet<String>();
    _genSet = new HashMap<String,Integer>();
    _inSet = new HashMap<String,Integer>();
    _outSet = new HashMap<String,Integer>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public void addUse(String use)
  {
    _useSet.add(use);
  }

  public void addGen(String gen, int lineNumber)
  {
    _genSet.put(gen, lineNumber);
  }

  public void addIn(String in, Integer lineNumber)
  {
    _inSet.put(in, lineNumber);
  }

  public void addOut(String out, Integer lineNumber)
  {
    _outSet.put(out, lineNumber);
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
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  public boolean computeSets(Collection<SootNode> parents)
  {
    Map<String,Integer> oldOutSet = getOutSet();

    _inSet = new HashMap<String,Integer>();
    for(SootNode parent : parents)
      _inSet.putAll(parent._dataFlow.getOutSet());

    _outSet = new HashMap<String,Integer>();
    _outSet.putAll(getInSet());
    _outSet = removeKillSet(getOutSet());
    _outSet.putAll(getGenSet());

    if(CompilerDirectives.DEBUG)
    {
      EasyLogger.log(Level.FINE, "" + !_outSet.equals(oldOutSet));
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
  String printReachingDefinitions(int lineNumber)
  {
    String result;
    if(lineNumber == -1)
      result = "Line entry\t";
    else
      result = "Line " + lineNumber + "\t";
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

  String printDU(int lineNumber)
  {
    String result = "";

    for(String use : getUseSet())
    {
      int definitionLine = getInSet().get(use);
      if(definitionLine == -1)
        result += " entry, " + use + " -> ";
      else
        result += " " + definitionLine + ", " + use + " -> ";
      if(lineNumber == -1)
        result += "entry;";
      else
        result += lineNumber + ";";
      result += System.lineSeparator();
    }

    return result;
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
