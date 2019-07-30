package laser.cs610.project;

import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.*;
import soot.options.Options;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import laser.cs610.RelevanceEvaluator;
import laser.datastructures.Pair;
import laser.datastructures.soot.SootMethodNode;

public class UIAnalyzer
{
  // <editor-fold> FIELDS ******************************************************
  //private Map<String, ExceptionalUnitGraph> _cfgs;
  private Map<Pair<String, String>, SootMethodNode> _cleanMethods;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public UIAnalyzer(String sootClassPathAppend)
  {
    //_cfgs = new HashMap<String, ExceptionalUnitGraph>();
    _cleanMethods = new HashMap<Pair<String, String>, SootMethodNode>();

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
        if (LoadMethod(sm, sc)) { entryPoints.add(sm); }
      }
    }

    Scene.v().setEntryPoints(entryPoints);
  }

  private boolean LoadMethod(SootMethod sm, SootClass sc)
  {
    //Body b = sm.retrieveActiveBody();
    //_cfgs.put(sm.getName(), new ExceptionalUnitGraph(b));
    _cleanMethods.put(new Pair<String, String>
      (sm.getDeclaringClass().getShortName(), sm.getName()),
      new SootMethodNode(sm.getName(), sm));
    return sm.getName().equals("main");
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  public void AnalyzeMethod(SootMethod sm)
  {
    ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(sm.getActiveBody());
    Unit head = cfg.getHeads().get(0);

  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String args[])
  {
    UIAnalyzer analyzer = new UIAnalyzer(args[0]);
    RelevanceEvaluator evaluator = UIRelevanceEvaluator._singleton;

    for (SootMethodNode methodToEval : analyzer._cleanMethods.values())
    {
      //methodToEval._stubType = evaluator.evaluate(
      //  methodToEval, analyzer._cleanMethods);
    }

    for (SootMethod sm : Scene.v().getEntryPoints())
    {
      analyzer.AnalyzeMethod(sm);
    }
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
