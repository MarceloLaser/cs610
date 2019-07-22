package laser.cs610.project;

import java.util.Collections;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import soot.Body;
import soot.jimple.ReturnStmt;
import soot.SootClass;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.InvokeStmt;
import soot.Unit;
import laser.datastructures.soot.SootMethodNode;
import laser.datastructures.Pair;
import laser.datastructures.soot.SootMethodStubType;
import laser.cs610.RelevanceEvaluator;

public class UIRelevanceEvaluator
  implements RelevanceEvaluator
{
  // <editor-fold> FIELDS ******************************************************
  public final List<Pair<String, String>> _uiMethods
    = new LinkedList<Pair<String,String>>();
  public static final RelevanceEvaluator _singleton
    = new UIRelevanceEvaluator();
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  private UIRelevanceEvaluator()
  {
    _uiMethods.add(new Pair<String, String>("java.util.Scanner", "next"));
    _uiMethods.add(new Pair<String, String>("java.util.Scanner", "nextInt"));
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  public SootMethodStubType evaluate(
    SootMethodNode node, Map<String, SootMethodNode> methods)
  {
    if (node._stubType != SootMethodStubType.UNKNOWN)
      return node._stubType;

    List<SootMethodStubType> stmtEval = new LinkedList<SootMethodStubType>();
    List<Unit> exitPoints = new LinkedList<Unit>();
    List<Unit> uiPoints = new LinkedList<Unit>();
    Body b = node._sootMethodPointer.getActiveBody();

    for (Unit u : b.getUnits())
    {
      SootMethodStubType result = evaluateStmt(u, methods);
      stmtEval.add(result);
      if (u instanceof ReturnStmt)
        exitPoints.add(u);
      if (result == SootMethodStubType.USERINPUT)
      {
        uiPoints.add(u);
        node._uiStatements.put(u.getJavaSourceStartLineNumber(), u);
      }
    }

    if (stmtEval.contains(SootMethodStubType.RELEVANT))
      return SootMethodStubType.RELEVANT;
    if (Collections.frequency(stmtEval, SootMethodStubType.USERINPUT) > 1)
      return SootMethodStubType.RELEVANT;
    if (!stmtEval.contains(SootMethodStubType.RELEVANT) &&
      !stmtEval.contains(SootMethodStubType.USERINPUT))
      return SootMethodStubType.IRRELEVANT;
    if (exitPoints.isEmpty())
      return SootMethodStubType.RELEVANT;
    if (exitPoints.size() > 1)
      return SootMethodStubType.RELEVANT;
    //TODO this conditional needs to be validated; it PROBABLY doesn't work
    if (exitPoints.get(0).getUseBoxes().equals(uiPoints.get(0).getDefBoxes()))
      return SootMethodStubType.USERINPUT;
    return SootMethodStubType.RELEVANT;
  }

  private SootMethodStubType evaluateStmt(
    Unit u, Map<String, SootMethodNode> methods)
  {
    if (!(u instanceof InvokeStmt))
      return SootMethodStubType.IRRELEVANT;
    InvokeStmt castU = (InvokeStmt)u;
    SootMethod currentMethod = castU.getInvokeExpr().getMethod();
    String declaringClass = currentMethod.getDeclaringClass().getName();
    String methodName = currentMethod.getName();
    Pair<String, String> invokeCheck
      = new Pair<String, String>(declaringClass, methodName);

    if (_uiMethods.contains(invokeCheck))
      return SootMethodStubType.USERINPUT;
    if(!isApplicationClass(declaringClass))
      return SootMethodStubType.IRRELEVANT;
    SootMethodStubType result = methods.get(methodName)._stubType;
    if(result != SootMethodStubType.UNKNOWN)
      return result;
    return evaluate(methods.get(methodName), methods);
  }

  private boolean isApplicationClass(String name)
  {
    for (SootClass sc : Scene.v().getApplicationClasses())
    {
      if(sc.getName().equals(name))
        return true;
    }
    return false;
  }
  // </editor-fold> COMPUTATION ************************************************
}
