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
  private Map<String, Map<Integer, SootNode>> _cleanCfgs;
  private CallGraph _callGraph;
  private Map<String, SootNode> _cleanCallGraph;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public Analyzer(String sootClassPathAppend)
  {
    _cfgs = new HashMap<String, ExceptionalUnitGraph>();
    _cleanCfgs = new HashMap<String, Map<Integer,SootNode>>();
    _cleanCallGraph = new HashMap<String, SootNode>();
    _callGraph = new CallGraph();

    LoadClassesAndMethods(sootClassPathAppend);
    BuildCleanCallGraph();
    BuildCallGraph();
  }

  /** Loads all files into Soot Scene */
  private void LoadClassesAndMethods(String sootClassPathAppend)
  {
    String sootClassPath = Scene.v().getSootClassPath();
    sootClassPath += File.pathSeparator + sootClassPathAppend;
    Scene.v().setSootClassPath(sootClassPath);
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("jb", "use-original-names:true");
    for (SootClass sc : Scene.v().getClasses())
    {
      Scene.v().loadClassAndSupport(sc.getName());
      Scene.v().loadNecessaryClasses();
      for (SootMethod m : sc.getMethods())
      {
        Body b = m.retrieveActiveBody();
        _cfgs.put(m.getName(), new ExceptionalUnitGraph(b));
      }
    }
  }

  /** Find program entry points and parse methods to new call graph format */
  private void BuildCleanCallGraph()
  {
    List<SootMethod> entryPoints = new LinkedList<SootMethod>();
    for (SootClass sc : Scene.v().getApplicationClasses()) {
      for (SootMethod sm : sc.getMethods()) {
        if (sm.getName().equals("main")) {
          entryPoints.add(sm);
        }
        _cleanCallGraph.put(sm.getName(), new SootNode(sm.getName(), sm));
      }
    }
    Scene.v().setEntryPoints(entryPoints);
  }

  private void BuildCallGraph()
  {
    //TODO Change this to use an appropriate type analysis
    CHATransformer.v().transform();
    CallGraph callGraph = Scene.v().getCallGraph();
    for (Edge e : callGraph)
    {
      if (e.getSrc().method().isJavaLibraryMethod() ||
        e.getTgt().method().isJavaLibraryMethod())
        continue;

      if (e.getSrc().method().getDeclaringClass()
        .getPackageName().startsWith("android.") ||
        e.getTgt().method().getDeclaringClass().
        getPackageName().startsWith("android."))
        continue;

      _callGraph.addEdge(e);
    }
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  private void cleanMethod(String methodName)
  {
    ExceptionalUnitGraph cfg = _cfgs.get(methodName);
    Map<Integer, SootNode> cleanCfg = new HashMap<Integer, SootNode>();
    _cleanCfgs.put(methodName, cleanCfg);
    // add all nodes
    for(Unit u : cfg.getBody().getUnits())
    {
      SootNode newNode = new SootNode(u.getJavaSourceStartLineNumber());
      cleanCfg.put(newNode._lineNumber, newNode);
    }
    // add all transitions
    for(Unit u : cfg.getBody().getUnits())
    {
      for(Unit succ : cfg.getSuccsOf(u))
      {
        Integer predNum = u.getJavaSourceStartLineNumber();
        Integer succNum = succ.getJavaSourceStartLineNumber();
        cleanCfg.get(predNum)._controlFlow
          .addSuccessor(succNum, cleanCfg.get(succNum));
        cleanCfg.get(succNum)._controlFlow
          .addParent(predNum, cleanCfg.get(predNum));
      }
    }
    // remove irrelevant nodes
    for(Unit u : cfg.getBody().getUnits())
    {
      if(!isRelevant(u))
      {
        Integer toRemoveNum = u.getJavaSourceStartLineNumber();
        SootNode toRemove = cleanCfg.get(toRemoveNum);
        for(SootNode pred : toRemove._controlFlow.getParents().values())
        {
          for(SootNode succ : toRemove._controlFlow.getSuccessors().values())
          {
            Integer predNum = pred._lineNumber;
            Integer succNum = succ._lineNumber;
            cleanCfg.get(predNum)._controlFlow
              .addSuccessor(succNum, cleanCfg.get(succNum));
            cleanCfg.get(succNum)._controlFlow
              .addParent(predNum, cleanCfg.get(predNum));
            succ._controlFlow.removeParent(toRemoveNum);
          }
          pred._controlFlow.removeSuccessor(toRemoveNum);
        }
        cleanCfg.remove(toRemoveNum);
      }
    }
  }

  private void cleanModel()
  {
    // creating edges
    for(SootNode checkMethod : _cleanCallGraph.values())
    {
      Iterator<Edge> successorMethods =
        _callGraph.edgesOutOf(checkMethod._sootMethodPointer);
      while(successorMethods.hasNext())
      {
        SootMethod successorMethod =
          (SootMethod)successorMethods.next().getTgt();
        String successorName = successorMethod.getName();
        SootNode successor = _cleanCallGraph.get(successorName);
        checkMethod._callgraphControlFlow
          .addSuccessor(successorName, successor);
        successor._callgraphControlFlow
          .addParent(checkMethod._name, checkMethod);
      }
    }

    // remove irrelevant nodes
    for(SootNode toRemove : _cleanCallGraph.values())
    {
      if(_cleanCfgs.get(toRemove._name).isEmpty())
      {
        String toRemoveName = toRemove._name;
        for(SootNode pred : toRemove._callgraphControlFlow
          .getParents().values())
        {
          for(SootNode succ : toRemove._callgraphControlFlow
            .getSuccessors().values())
          {
            String predName = pred._name;
            String succName = succ._name;
            _cleanCallGraph.get(predName)._callgraphControlFlow
              .addSuccessor(succName, _cleanCallGraph.get(succName));
            _cleanCallGraph.get(succName)._callgraphControlFlow
              .addParent(predName, _cleanCallGraph.get(predName));
            succ._callgraphControlFlow.removeParent(toRemoveName);
          }
          pred._callgraphControlFlow.removeSuccessor(toRemoveName);
        }
        _cleanCallGraph.remove(toRemoveName);
      }
    }
  }

  private boolean isRelevant(Unit u)
  {
    if(!(u instanceof InvokeStmt))
      return false;
    InvokeStmt castU = (InvokeStmt)u;
    SootMethod currentMethod = castU.getInvokeExpr().getMethod();
    String declaringClass = currentMethod.getDeclaringClass().getName();
    String methodName = currentMethod.getName();
    if(declaringClass.equals("java.util.Scanner")
      && (methodName.equals("next") || methodName.equals("nextInt")))
      return true;
    return false;
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String args[])
  {
    Analyzer analyzer = new Analyzer(args[0]);
    for(String methodName : analyzer._cfgs.keySet())
    {
      analyzer.cleanMethod(methodName);
    }
    analyzer.cleanModel();
    analyzer.printModel(args[1], args[2]);
  }

  private void printModel(String path, String fileName)
  {
    String result = "digraph ui_model {" + System.lineSeparator();
    result += "	node [shape = rectangle]; entry exit;" + System.lineSeparator();
    result += "	node [shape = circle];" + System.lineSeparator();
    result += "" + System.lineSeparator();
    for(SootNode checkMethod : _cleanCallGraph.values())
    {
      String originClassName = checkMethod._sootMethodPointer
        .getClass().getName();
      String originMethodName = checkMethod._name;
      String originNodeName = originClassName + ":" + originMethodName;
      if(checkMethod._callgraphControlFlow.getParents().isEmpty())
      {
        result += "entry -> " + originNodeName + ";" + System.lineSeparator();
        continue;
      }
      for(SootNode checkStatement : _cleanCfgs.get(checkMethod._name).values())
      {
        if(checkStatement._controlFlow.getSuccessors().isEmpty())
        {

        }
      }
    }
  }
  // </editor-fold> IO OPERATIONS **********************************************

  // <editor-fold> DEBUG *******************************************************
  protected void logUnit(Unit u)
  {
    EasyLogger.log(Level.FINEST, u.getJavaSourceStartLineNumber() + " : "
      + u.branches() + " : " + u.getClass() + " : "
      + u.fallsThrough() + " : " + u.toString());
  }

  protected void logBlock(Block bl)
  {
    EasyLogger.log(Level.FINEST, bl.toString());
  }

  protected void logLoop(Loop lp)
  {
    String loop = "Loop: " + System.lineSeparator();
    loop += "Head: " + lp.getHead() + System.lineSeparator();
    loop += "Tails: ";
    for(Stmt ex : lp.getLoopExits())
      loop += ex + " : ";
    loop += System.lineSeparator();
    loop += "Body: " + System.lineSeparator();
    for(Stmt bod : lp.getLoopStatements())
      loop += bod + System.lineSeparator();

    EasyLogger.log(Level.FINEST, loop);
  }
  // </editor-fold> DEBUG ******************************************************
}
