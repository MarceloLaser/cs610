package laser.cs610.hw2;

import soot.jimple.internal.JAssignStmt;
import laser.datastructures.soot.*;
import laser.util.CompilerDirectives;
import laser.util.EasyLogger;
import soot.ValueBox;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;
import java.util.*;
import java.util.logging.Level;

public class RdduFacade
{
  // <editor-fold> FIELDS ******************************************************
  private Map<Integer, SootNode> _nodes;
  private Set<String> _definitions;
  private ExceptionalUnitGraph _sootCfg;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public RdduFacade(ExceptionalUnitGraph cfg)
  {
    _nodes = new HashMap<Integer, SootNode>();
    _definitions = new HashSet<String>();
    setSootCfg(cfg);
    initialize();
    while(compute());
  }

  public void initialize()
  {
    Iterator<Unit> iterator = _sootCfg.iterator();

    while(iterator.hasNext())
    {
      Unit current = iterator.next();
      addNode(current);
      findSuccessors(current, _sootCfg);
      findDefinitions(current);
    }

    iterator = _sootCfg.iterator();

    while(iterator.hasNext())
    {
      findUses(iterator.next());
    }
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public boolean containsNode(Integer lineNumber)
  {
    return _nodes.containsKey(lineNumber);
  }

  public SootNode getNode(Integer lineNumber)
  {
    return _nodes.get(lineNumber);
  }

  public void addNode(Unit node)
  {
    int currentLineNumber = node.getJavaSourceStartLineNumber();
    if(!containsNode(currentLineNumber))
      _nodes.put(currentLineNumber, new SootNode(currentLineNumber));
  }

  public Set<SootNode> allNodes()
  {
    return new HashSet<SootNode>(_nodes.values());
  }

  public Map<Integer, SootNode> nodesMap()
  {
    return new HashMap<Integer, SootNode>(_nodes);
  }

  public void setSootCfg(ExceptionalUnitGraph cfg)
  {
    _sootCfg = cfg;
  }

  public void addDefinition(String definitionValue)
  {
    _definitions.add(definitionValue);
  }

  public boolean containsDefinition(String definitionValue)
  {
    return _definitions.contains(definitionValue);
  }
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  public boolean compute()
  {
    boolean change = false;
    Collection<SootNode> successors;
    Queue<SootNode> computeQueue = new LinkedList<SootNode>();
    Set<SootNode> computed = new HashSet<SootNode>();
    SootNode current;

    for(Unit u : _sootCfg.getHeads())
      computeQueue.add(getNode(u.getJavaSourceStartLineNumber()));

    while(computeQueue.size() > 0)
    {
      current = computeQueue.poll();
      successors = current._controlFlow.getSuccessors().values();
      for(SootNode successor : successors)
      {
        if(!computed.contains(successor))
          computeQueue.add(successor);
      }

      if(!change)
        change = current.computeSets();
      current.computeSets();
      computed.add(current);
    }

    if(CompilerDirectives.DEBUG)
    {
      EasyLogger.log(Level.FINE, "Computation Step : " + change);
    }

    return change;
  }

  private void findSuccessors(Unit current, ExceptionalUnitGraph cfg)
  {
    int currentLineNumber = current.getJavaSourceStartLineNumber();
    int successorLineNumber;
    SootNode currentNode = getNode(currentLineNumber);
    SootNode successorNode;

    for(Unit successor : cfg.getUnexceptionalSuccsOf(current))
    {
      successorLineNumber = successor.getJavaSourceStartLineNumber();
      if(successorLineNumber == currentLineNumber)
        continue;
      addNode(successor);

      successorNode = getNode(successorLineNumber);
      currentNode._controlFlow.addSuccessor(successorLineNumber, successorNode);
      successorNode._controlFlow.addParent(currentLineNumber, currentNode);
    }
  }

  private void findDefinitions(Unit current)
  {
    String definitionValue;
    String localVariablePattern = "l[0-9]+";
    int currentLineNumber = current.getJavaSourceStartLineNumber();

    for(ValueBox definition : current.getDefBoxes())
    {
      if(CompilerDirectives.DEBUG)
      {
        EasyLogger.log(Level.FINE, "Evaluating definition " + definition);
      }
      definitionValue = definition.getValue().toString();
      if(definitionValue.contains("stack") ||
        definitionValue.matches(localVariablePattern))
        continue;
      getNode(currentLineNumber)._dataFlow
        .addGen(definitionValue, currentLineNumber);
      addDefinition(definitionValue);

      if(current instanceof JAssignStmt)
      {
        JAssignStmt assignment = (JAssignStmt)current;
        String assigned = assignment.rightBox.getValue().toString();

        if(CompilerDirectives.DEBUG)
        {
          EasyLogger.log(Level.INFO, "Trying to save constant "
            + definitionValue + " of value " + assigned);
        }

        try
        {
          Integer assignedValue = Integer.parseInt(assigned);
          getNode(currentLineNumber)._dataFlow
            .addConstant(definitionValue, assignedValue);
        }
        catch(NumberFormatException e) { };
      }
    }
  }

  private void findUses(Unit current)
  {
    String usageValue;
    int currentLineNumber = current.getJavaSourceStartLineNumber();
    for(ValueBox usage : current.getUseBoxes())
    {
      usageValue = usage.getValue().toString();
      if(containsDefinition(usageValue))
        getNode(currentLineNumber)._dataFlow.addUse(usageValue);
    }
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public String printNodes()
  {
    String result = "";
    for(SootNode node : _nodes.values())
      result += node.printNode();

    return result;
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
