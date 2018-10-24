package laser.cs610.hw4;

import java.util.logging.Level;
import laser.util.EasyLogger;
import laser.util.CompilerDirectives;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.jimple.internal.JIfStmt;
import com.microsoft.z3.Context;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import laser.cs610.UnitType;
import laser.cs610.hw3.SlicerDriver;
import laser.datastructures.soot.SootNode;

public class SymExDriver
{
  // <editor-fold> FIELDS ******************************************************
  private ExceptionalUnitGraph _cfgSoot;
  private Map<Integer, SootNode> _pdg;
  private Integer _evaluationLine;
  private boolean _feasible;
  private Context _context;
  private List<Unit> _traversed;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SymExDriver(Integer evaluationLine, ExceptionalUnitGraph cfgSoot)
  {
    SlicerDriver pdgBuilder = new SlicerDriver(cfgSoot);
    _cfgSoot = cfgSoot;
    _pdg = pdgBuilder.getPdg();
    _evaluationLine = evaluationLine;
    Unit entry = _cfgSoot.getHeads().get(0);
    _context = new Context();
    _traversed = new LinkedList<Unit>();
    compute(entry, new LinkedList<BoolExpr>());

    SootNode evaluatedNode = _pdg.get(_evaluationLine);
    for(BoolExpr condition : evaluatedNode.getPathCondition())
    {
      if(CompilerDirectives.DEBUG)
      {
        EasyLogger.log(Level.INFO,
          "Checking condition: " + condition.toString());
      }
      Solver solver = _context.mkSolver();
      solver.add(condition);
      Status solved = solver.check();
      if(solved == Status.SATISFIABLE)
      {
        _feasible = true;
        _context.close();
        return;
      }
    }
    _feasible = false;

    _context.close();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public boolean isFeasible()
  {
    return _feasible;
  }
  // </editor-fold> ACCESSORS ***************************************************

  // <editor-fold> COMPUTATION *************************************************
  private void compute(Unit u, List<BoolExpr> pathCondition)
  {
    List<Unit> successors = _cfgSoot.getUnexceptionalSuccsOf(u);
    if(CompilerDirectives.DEBUG)
    {
      EasyLogger.log(Level.INFO, "Computing Unit: " + logUnit(u));
      for(Unit successor : successors)
        EasyLogger.log(Level.INFO, "Succeeded by: " + logUnit(successor));
    }
    assignPathCondition(u, pathCondition);
    _traversed.add(u);

    if(!u.branches()) //TODO base conditions
    {
      if(u.fallsThrough() || UnitType.getUnitType(u) == UnitType.JGOTOSTMT)
      {
        Unit successor = successors.get(0);
        if(!_traversed.contains(successor))
          compute(successor, pathCondition);
      }
      return;
    }

    evaluateExpr(u, pathCondition);
  }

  private void evaluateExpr(Unit u, List<BoolExpr> pathCondition)
  {
    UnitType uType = UnitType.getUnitType(u);
    switch(uType)
    {
      case JIFSTMT:
        evaluateIfStmt(u, pathCondition);
        break;
      case JLOOKUPSWITCHSTMT:
        evaluateLookupSwitchStmt(u);
        break;
      case JTABLESWITCHSTMT:
        evaluateTableSwitchStmt(u);
        break;
    }
  }

  private void evaluateIfStmt(Unit u, List<BoolExpr> pathCondition)
  {
    EvaluationPO parameterObject = new EvaluationPO(u, pathCondition, _context);
    EvaluationPO result;
    JIfStmt current = (JIfStmt)u;

    try
    {
      result = EvaluateIfStmtMO.evaluateIfStmt(parameterObject);
    }
    catch(Exception e)
    {
      System.exit(1);
      return;
    }

    List<Unit> successors = _cfgSoot.getUnexceptionalSuccsOf(u);
    successors.remove(current.getTargetBox().getUnit());
    compute(current.getTargetBox().getUnit(), result._pathConditionPositive);
    compute(successors.get(0), result._pathConditionNegative);
  }

  private void evaluateLookupSwitchStmt(Unit u)
  {

  }

  private void evaluateTableSwitchStmt(Unit u)
  {

  }

  private void assignPathCondition(Unit u, List<BoolExpr> pathCondition)
  {
    SootNode current = _pdg.get(u.getJavaSourceStartLineNumber());

    if(current == null)
      return;

    for(BoolExpr expression : pathCondition)
      current.addPathCondition(expression);
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> DEBUG *******************************************************
  private String logUnit(Unit u)
  {
    return u.getJavaSourceStartLineNumber() + " : "
      + u.branches() + " : " + u.getClass() + " : "
      + u.fallsThrough() + " : " + u.toString();
  }
  // </editor-fold> DEBUG ******************************************************
}
