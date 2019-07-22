package laser.cs610;

import java.util.Map;
import laser.datastructures.soot.SootMethodNode;
import laser.datastructures.soot.SootMethodStubType;

public interface RelevanceEvaluator
{
  public SootMethodStubType evaluate(
    SootMethodNode node, Map<String, SootMethodNode> methods);
}
