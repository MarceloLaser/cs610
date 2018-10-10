package laser.cs610.hw1;

import java.util.HashSet;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import soot.Unit;
import soot.util.Chain;
import soot.jimple.internal.*;
import soot.jimple.*;
import laser.datastructures.soot.*;
import laser.util.CompilerDirectives;
import laser.util.EasyLogger;
import laser.cs610.*;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Iterators;

public class CfgFacade
{
  // <editor-fold> FIELDS ******************************************************
  private Map<Integer,SootNode> _nodes;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public CfgFacade()
  {
    _nodes = new HashMap<Integer,SootNode>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public boolean containsNode(Integer lineNumber)
  {
    return _nodes.containsKey(lineNumber);
  }

  public SootNode addNode(SootNode node)
  {
    if(!containsNode(node._lineNumber))
      return _nodes.put(node._lineNumber, node);
    SootNode newNode = getNode(node._lineNumber);
    newNode._controlFlow.copyTransitions(node);
    return newNode;
  }

  public SootNode addNode(Integer lineNumber)
  {
    if(!containsNode(lineNumber))
      _nodes.put(lineNumber, new SootNode(lineNumber));
    return getNode(lineNumber);
  }

  public SootNode addNode(Unit current)
  {
    int currentLineNumber = current.getJavaSourceStartLineNumber();
    return addNode(currentLineNumber);
  }

  public SootNode addNode(Integer currentLineNumber,
    Integer targetLineNumber, String label)
  {
    SootNode newNode = addNode(currentLineNumber);
    newNode._controlFlow.addTransition(targetLineNumber,label);
    return newNode;
  }

  public SootNode addNode(Integer currentLineNumber, Unit target, String label)
  {
    int targetLineNumber = target.getJavaSourceStartLineNumber();
    return addNode(currentLineNumber, targetLineNumber, label);
  }

  public SootNode addNode(Unit source, Integer targetLineNumber, String label)
  {
    int currentLineNumber = source.getJavaSourceStartLineNumber();
    return addNode(currentLineNumber, targetLineNumber, label);
  }

  public SootNode addNode(Unit source, Unit target, String label)
  {
    int currentLineNumber = source.getJavaSourceStartLineNumber();
    return addNode(currentLineNumber, target, label);
  }

  public SootNode getNode(Integer lineNumber)
  {
    return _nodes.get(lineNumber);
  }
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  public Collection<SootNode> compute(Chain<Unit> units)
  {
    PeekingIterator<Unit> it = Iterators.peekingIterator(units.iterator());

    for (PeekingIterator<Unit> i = it; i.hasNext(); )
    {
      Unit current = i.next();
      Unit next = null;
      int currentLineNumber = current.getJavaSourceStartLineNumber();
      int nextLineNumber;

      if(CompilerDirectives.DEBUG)
        logUnit(current);

      if(!i.hasNext())
      {
        addNode(current, -1, null);
        continue;
      }
      next = i.peek();
      nextLineNumber = next.getJavaSourceStartLineNumber();
      boolean sameBasicBlock = currentLineNumber == nextLineNumber;

      if(current.fallsThrough() && !current.branches() && !sameBasicBlock)
      {
        if(CompilerDirectives.DEBUG)
          logNode(current, next, null);
        addNode(current, next, null);
        continue;
      }

      evaluateStatement(current, next);
    }

    if(CompilerDirectives.DEBUG)
      logTransitions();

    return new HashSet<SootNode>(_nodes.values());
  }

  private void evaluateStatement(Unit u, Unit next)
  {
    switch(UnitType.getUnitType(u))
    {
      case JASSIGNSTMT:
        if(CompilerDirectives.DEBUG)
          EasyLogger.log(Level.FINEST, "Skip JAssignStmt evaluation");
        break;
      case JIDENTITYSTMT:
        if(CompilerDirectives.DEBUG)
          EasyLogger.log(Level.FINEST, "Skip JIdentityStmt evaluation");
        break;
      case JINVOKESTMT:
        if(CompilerDirectives.DEBUG)
          EasyLogger.log(Level.FINEST, "Skip JInvokeStmt evaluation");
        break;
      case JIFSTMT:
        evaluateIfStmt((JIfStmt)u, next);
        break;
      case JGOTOSTMT:
        evaluateGoToStmt((JGotoStmt)u, next);
        break;
      case JRETURNVOIDSTMT:
        addNode(u, -1, null);
        break;
      case JTABLESWITCHSTMT:
        evaluateTableSwitchStmt((JTableSwitchStmt)u, next);
        break;
      case JLOOKUPSWITCHSTMT:
        evaluateLookupSwitchStmt((JLookupSwitchStmt)u, next);
    }
  }

  private void evaluateLookupSwitchStmt(JLookupSwitchStmt u, Unit next)
  {
    List<IntConstant> lookupValues = u.getLookupValues();

    for(int i = 0; i < lookupValues.size(); i++)
    {
      addNode(u, u.getTarget(i), ""+u.getLookupValue(i));
    }
    addNode(u, u.getDefaultTarget(), "Default");
  }

  private void evaluateTableSwitchStmt(JTableSwitchStmt u, Unit next)
  {
    int lowerSwitchBoundary = u.getLowIndex();
    int higherSwitchBoundary = u.getHighIndex();

    for(int i = lowerSwitchBoundary; i < higherSwitchBoundary; i++)
    {
      addNode(u, u.getTarget(i), ""+i);
    }
    addNode(u, u.getDefaultTarget(), "Default");
  }

  private void evaluateGoToStmt(JGotoStmt u, Unit next)
  {
    addNode(u, u.getTargetBox().getUnit(), null);

    if(CompilerDirectives.DEBUG)
    {
      int currentLineNumber = u.getJavaSourceStartLineNumber();
      int targetLineNumber = u.getTargetBox()
        .getUnit().getJavaSourceStartLineNumber();
      EasyLogger.log(Level.FINEST, currentLineNumber + " : " +
        targetLineNumber);
    }
  }

  private void evaluateIfStmt(JIfStmt u, Unit next)
  {
    Unit target;
    AbstractBinopExpr expression;
    String symbol = null;

    target = u.getTargetBox().getUnit();
    expression = (AbstractBinopExpr)u.getCondition();

    try
    {
      symbol = (String)expression.getClass().getMethod("getSymbol")
        .invoke(expression);
      symbol = symbol.trim();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    addNode(u,target,symbol);
    addNode(u,next, "!" + symbol);
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> DEBUG *******************************************************
  private void logTransitions()
  {
    Collection<SootTransition> transitions;
    for(SootNode node : _nodes.values())
    {
      transitions = node._controlFlow.getTransitions();
      for(SootTransition transition : transitions)
      {
        EasyLogger.log(Level.INFO, node._lineNumber + " : "
          + transition._targetLineNumber + " : "
          + transition._transitionLabel);
      }
    }
  }

  private void logUnit(Unit u)
  {
    EasyLogger.log(Level.INFO, u.getJavaSourceStartLineNumber() + " : "
      + u.branches() + " : " + u.getClass() + " : "
      + u.fallsThrough() + " : " + u.toString());
  }

  private void logNode(Unit source, Unit target, String label)
  {
    EasyLogger.log(Level.FINE, source.getJavaSourceStartLineNumber()
      + " " + target.getJavaSourceStartLineNumber() + " " + label);
  }
  // </editor-fold> DEBUG ******************************************************
}
