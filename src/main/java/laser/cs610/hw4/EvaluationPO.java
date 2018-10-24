package laser.cs610.hw4;

import com.microsoft.z3.Context;
import com.microsoft.z3.BoolExpr;
import java.util.List;
import soot.Unit;

public class EvaluationPO
{
  public final Unit _u;
  public final List<BoolExpr> _pathConditionPositive;
  public final List<BoolExpr> _pathConditionNegative;
  public final Context _ctx;

  public EvaluationPO(Unit u, List<BoolExpr> pathCondition, Context ctx)
  {
    _u = u;
    _pathConditionPositive = pathCondition;
    _pathConditionNegative = null;
    _ctx = ctx;
  }

  public EvaluationPO(Unit u, List<BoolExpr> pcPos,
    List<BoolExpr> pcNeg, Context ctx)
  {
    _u = u;
    _pathConditionPositive = pcPos;
    _pathConditionNegative = pcNeg;
    _ctx = ctx;
  }
}
