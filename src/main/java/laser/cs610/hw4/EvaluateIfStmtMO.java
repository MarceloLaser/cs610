package laser.cs610.hw4;

import java.util.logging.Level;
import laser.util.EasyLogger;
import laser.util.CompilerDirectives;
import soot.jimple.EqExpr;
import soot.jimple.NeExpr;
import soot.jimple.LtExpr;
import soot.jimple.LeExpr;
import soot.jimple.GtExpr;
import soot.jimple.GeExpr;
import soot.jimple.IntConstant;
import soot.jimple.BinopExpr;
import soot.jimple.internal.JIfStmt;
import soot.Value;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.ArithExpr;
import java.util.List;
import java.util.LinkedList;

public class EvaluateIfStmtMO
{
  public static EvaluationPO evaluateIfStmt(EvaluationPO parameterObject)
    throws Exception
  {
    List<BoolExpr> pcPos = parameterObject._pathConditionPositive;
    List<BoolExpr> pcNeg = new LinkedList<BoolExpr>();
    JIfStmt current = (JIfStmt)parameterObject._u;
    Context ctx = parameterObject._ctx;
    ArithExpr operand1;
    ArithExpr operand2;
    BoolExpr operation;
    BoolExpr antiOperation;

    Value cond = current.getCondition();

    final Value op1 = ((BinopExpr) cond).getOp1();
    final Value op2 = ((BinopExpr) cond).getOp2();

    if(op2 instanceof IntConstant)
      operand2 = ctx.mkInt(((IntConstant) op2).value);
    else
      operand2 = ctx.mkIntConst(op2.toString());
    if(op1 instanceof IntConstant)
      operand1 = ctx.mkInt(((IntConstant) op1).value);
    else
      operand1 = ctx.mkIntConst(op1.toString());

    if(CompilerDirectives.DEBUG)
    {
      String toLog = "Evaluating condition: " + cond.toString() + " of type ";
      toLog += cond.getClass() + " from unit ";
      toLog += current.getJavaSourceStartLineNumber();
      EasyLogger.log(Level.INFO, toLog);
    }

    if(cond instanceof EqExpr)
    {
      operation = ctx.mkEq(operand1, operand2);
      antiOperation = ctx.mkDistinct(operand1, operand2);
    }
    else if(cond instanceof NeExpr)
    {
      operation = ctx.mkDistinct(operand1, operand2);
      antiOperation = ctx.mkEq(operand1, operand2);
    }
    else if(cond instanceof LtExpr)
    {
      operation = ctx.mkLt(operand1, operand2);
      antiOperation = ctx.mkGe(operand1, operand2);
    }
    else if(cond instanceof LeExpr)
    {
      operation = ctx.mkLe(operand1, operand2);
      antiOperation = ctx.mkGt(operand1, operand2);
    }
    else if(cond instanceof GtExpr)
    {
      operation = ctx.mkGt(operand1, operand2);
      antiOperation = ctx.mkLe(operand1, operand2);
    }
    else if(cond instanceof GeExpr)
    {
      operation = ctx.mkGe(operand1, operand2);
      antiOperation = ctx.mkLt(operand1, operand2);
    }
    else
    {
      if(CompilerDirectives.DEBUG)
      {
        String toLog = "Error in evaluating line ";
        toLog += current.getJavaSourceStartLineNumber() + " of type ";
        toLog += cond.getClass();
        EasyLogger.log(Level.SEVERE, toLog);
      }
      System.out.println(cond.getClass());
      throw new Exception();
    }

    if(CompilerDirectives.DEBUG)
    {
      String toLog = "Conditions found: " + operation + ":" + antiOperation;
      EasyLogger.log(Level.INFO, toLog);
    }

    for(BoolExpr condition : pcPos)
    {
      pcNeg.add(ctx.mkAnd(condition, antiOperation));
      condition = ctx.mkAnd(condition, operation);
    }

    if(pcPos.isEmpty())
    {
      pcNeg.add(antiOperation);
      pcPos.add(operation);
    }

    return new EvaluationPO(current, pcPos, pcNeg, ctx);
  }
}
