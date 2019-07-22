package laser.cs610.project;

import soot.*;
import soot.options.Options;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import laser.cs610.RelevanceEvaluator;
import laser.datastructures.soot.SootMethodNode;

public class UIAnalyzer
{
  // <editor-fold> FIELDS ******************************************************
  //private Map<String, ExceptionalUnitGraph> _cfgs;
  private Map<String, SootMethodNode> _cleanMethods;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public UIAnalyzer(String sootClassPathAppend)
  {
    //_cfgs = new HashMap<String, ExceptionalUnitGraph>();
    _cleanMethods = new HashMap<String, SootMethodNode>();

    InitializeSoot(sootClassPathAppend);
    LoadClasses();
  }

  private void InitializeSoot(String sootClassPathAppend)
  {
    String sootClassPath = Scene.v().getSootClassPath();
    sootClassPath += File.pathSeparator + sootClassPathAppend;
    Scene.v().setSootClassPath(sootClassPath);
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("jb", "use-original-names:true");
  }

  /** Loads all files into Soot Scene */
  private void LoadClasses()
  {
    List<SootMethod> entryPoints = new LinkedList<SootMethod>();

    for (SootClass sc : Scene.v().getClasses())
    {
      Scene.v().loadClassAndSupport(sc.getName());
      Scene.v().loadNecessaryClasses();
      for (SootMethod sm : sc.getMethods())
      {
        if (LoadMethod(sm)) { entryPoints.add(sm); }
      }
    }

    Scene.v().setEntryPoints(entryPoints);
  }

  private boolean LoadMethod(SootMethod sm)
  {
    //Body b = sm.retrieveActiveBody();
    //_cfgs.put(sm.getName(), new ExceptionalUnitGraph(b));
    _cleanMethods.put(sm.getName(), new SootMethodNode(sm.getName(), sm));
    return sm.getName().equals("main");
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String args[])
  {
    UIAnalyzer analyzer = new UIAnalyzer(args[0]);
    RelevanceEvaluator evaluator = UIRelevanceEvaluator._singleton;

    for (SootMethodNode methodToEval : analyzer._cleanMethods.values())
    {
      methodToEval._stubType = evaluator.evaluate(
        methodToEval, analyzer._cleanMethods);
    }
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
