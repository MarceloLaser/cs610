package laser.cs610;

import soot.jimple.internal.*;
import soot.*;

public enum UnitType
{
  JIDENTITYSTMT,
  JASSIGNSTMT,
  JINVOKESTMT,
  JIFSTMT,
  JGOTOSTMT,
  JRETURNVOIDSTMT,
  JTABLESWITCHSTMT,
  JLOOKUPSWITCHSTMT;

  public static UnitType getUnitType(Unit u)
  {
    if(u instanceof JAssignStmt) return JASSIGNSTMT;
    if(u instanceof JIdentityStmt) return JIDENTITYSTMT;
    if(u instanceof JInvokeStmt) return JINVOKESTMT;
    if(u instanceof JIfStmt) return JIFSTMT;
    if(u instanceof JGotoStmt) return JGOTOSTMT;
    if(u instanceof JReturnVoidStmt) return JRETURNVOIDSTMT;
    if(u instanceof JTableSwitchStmt) return JTABLESWITCHSTMT;
    if(u instanceof JLookupSwitchStmt) return JLOOKUPSWITCHSTMT;
    return null;
  }
}
