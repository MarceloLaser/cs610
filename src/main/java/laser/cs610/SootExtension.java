package laser.cs610;

import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.*;
import soot.options.Options;
import soot.util.Chain;
import java.io.File;

public abstract class SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  protected SootClass _sc;
  protected SootMethod _sm;
  protected Body _b;
  protected Chain<Unit> _units;
  protected ExceptionalUnitGraph _cfgSoot;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  protected SootExtension(String className, String sootClassPathAppend)
  {
    initialize(className, sootClassPathAppend);
  }

  private void initialize(String className, String sootClassPathAppend)
  {
    String sootClassPath = Scene.v().getSootClassPath();
    sootClassPath += File.pathSeparator + sootClassPathAppend;

    Scene.v().setSootClassPath(sootClassPath);
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("jb", "use-original-names:true");
    _sc = Scene.v().loadClassAndSupport(className);
    Scene.v().loadNecessaryClasses();
    _sc.setApplicationClass();
    _sm = _sc.getMethodByName("main");
    _b = _sm.retrieveActiveBody();
    _units = _b.getUnits();
    _cfgSoot = new ExceptionalUnitGraph(_b);
  }
  // </editor-fold> INITIALIZATION *********************************************
}
