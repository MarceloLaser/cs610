package laser.datastructures.soot;

import soot.SootClass;
import java.util.HashMap;
import java.util.Map;
import soot.Unit;
import soot.SootMethod;

public class SootMethodNode
  extends SootNode
{
  // <editor-fold> FIELDS ******************************************************
  public SootMethodStubType _stubType;
  public final Map<Integer, Unit> _uiStatements;
  public final SootClass _declaringClass;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootMethodNode(String name, SootMethod sootMethodPointer)
  {
    super(0, name, sootMethodPointer);
    _stubType = SootMethodStubType.UNKNOWN;
    _declaringClass = sootMethodPointer.getDeclaringClass();
    _uiStatements = new HashMap<Integer, Unit>();
  }
  // </editor-fold> INITIALIZATION *********************************************
}
