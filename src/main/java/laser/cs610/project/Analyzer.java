package laser.cs610.project;

import java.util.Iterator;
import soot.jimple.InvokeStmt;
import java.util.HashMap;
import java.util.Map;
import laser.datastructures.soot.SootNode;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.Edge;
import java.util.LinkedList;
import soot.jimple.toolkits.callgraph.CallGraph;
import java.util.List;
import laser.util.EasyLogger;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.Block;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.Stmt;
import soot.*;
import soot.options.Options;
import java.io.File;
import java.util.logging.Level;

public class Analyzer
{
  // <editor-fold> FIELDS ******************************************************
  private Map<String, ExceptionalUnitGraph> _cfgs;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public Analyzer(String sootClassPathAppend)
  {
    SetUpSoot(sootClassPathAppend);
    LoadClasses();
    LoadMethods();
  }

  /** Set up Soot options */
  private void SetUpSoot(String sootClassPathAppend)
  {
    String sootClassPath = Scene.v().getSootClassPath();
    sootClassPath += File.pathSeparator + sootClassPathAppend;
    Scene.v().setSootClassPath(sootClassPath);
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("jb", "use-original-names:true");
  }

  /** Loads all classes into Soot Scene */
  private void LoadClasses()
  {
    for (SootClass sc : Scene.v().getClasses())
    {
      Scene.v().loadClassAndSupport(sc.getName());
    }

    Scene.v().loadNecessaryClasses();
  }

  /** Loads all methods into Soot Scene */
  private void LoadMethods()
  {
    List<SootMethod> entryPoints = new LinkedList<SootMethod>();

    for (SootClass sc : Scene.v().getClasses())
    {
      for (SootMethod sm : sc.getMethods())
      {
        Body b = sm.retrieveActiveBody();
        _cfgs.put(sm.getName(), new ExceptionalUnitGraph(b));
        if (sm.getName().equals("main")) {
          entryPoints.add(sm);
        }
      }
    }

    Scene.v().setEntryPoints(entryPoints);
  }
  // </editor-fold> INITIALIZATION *********************************************
}
