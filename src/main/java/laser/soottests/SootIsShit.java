package laser.soottests;

import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.ProgramDependenceGraph;
import laser.cs610.SootExtension;

public class SootIsShit extends SootExtension
{
  public SootIsShit(String className, String sootClassPathAppend,
    String methodName)
  {
    super(className, sootClassPathAppend, methodName);
  }

  public static void main(String args[])
  {
    SootIsShit itReallyIs = new SootIsShit(args[0], args[1], args[2]);
    ProgramDependenceGraph pdg = new HashMutablePDG(itReallyIs._cfgSoot);

    System.out.println(pdg.getPDGRegions());
    System.out.println("--------------------------");
    System.out.println("--------------------------");
    System.out.println("--------------------------");
  }
}
