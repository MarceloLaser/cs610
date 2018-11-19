package laser.cs610;

import laser.util.CompilerDirectives;
import laser.util.EasyLogger;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.Block;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.Stmt;
import soot.*;
import soot.options.Options;
import soot.util.Chain;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;

public abstract class SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  protected String _className;
  protected SootClass _sc;
  protected SootMethod _sm;
  protected Body _b;
  protected Chain<Unit> _units;
  protected ExceptionalUnitGraph _cfgSoot;
  protected ExceptionalBlockGraph _blockCfgSoot;
  protected LoopNestTree _loops;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  protected SootExtension(String className, String sootClassPathAppend,
    String methodName)
  {
    initialize(className, sootClassPathAppend, methodName);
  }

  private void initialize(String className, String sootClassPathAppend,
    String methodName)
  {
    String sootClassPath = Scene.v().getSootClassPath();
    sootClassPath += File.pathSeparator + sootClassPathAppend;
    _className = className;

    Scene.v().setSootClassPath(sootClassPath);
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("jb", "use-original-names:true");
    _sc = Scene.v().loadClassAndSupport(className);
    Scene.v().loadNecessaryClasses();
    _sc.setApplicationClass();

    for (SootMethod m: _sc.getMethods())
    {
			m.retrieveActiveBody();
		}

    _sm = _sc.getMethodByName(methodName);
    _b = _sm.retrieveActiveBody();
    _units = _b.getUnits();
    _cfgSoot = new ExceptionalUnitGraph(_b);
    _blockCfgSoot = new ExceptionalBlockGraph(_cfgSoot);
    _loops = new LoopNestTree(_b);

    if(CompilerDirectives.DEBUG)
    {
      for(Unit u : _units)
      {
        logUnit(u);
      }

      Iterator<Block> itBl = _blockCfgSoot.iterator();
      while(itBl.hasNext())
        logBlock(itBl.next());

      Iterator<Loop> itLp = _loops.iterator();
      while(itLp.hasNext())
        logLoop(itLp.next());
    }
  }
  // </editor-fold> INITIALIZATION *********************************************

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
