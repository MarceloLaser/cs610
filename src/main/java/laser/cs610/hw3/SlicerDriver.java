package laser.cs610.hw3;

import java.util.Map;
import soot.toolkits.graph.ExceptionalUnitGraph;
import laser.datastructures.soot.SootNode;
import laser.cs610.hw2.RdduFacade;

public class SlicerDriver
{
  // <editor-fold> FIELDS ******************************************************
  private Integer _sliceLine;
  private String _sliceVariable;
  private ExceptionalUnitGraph _sootCfg;
  private Map<Integer, SootNode> _pdg;

  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SlicerDriver(Integer sliceLine, String sliceVariable,
    ExceptionalUnitGraph sootCfg)
  {
    _sliceLine = sliceLine;
    _sliceVariable = sliceVariable;
    _sootCfg = sootCfg;
    buildPdg();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  private void buildPdg()
  {
    _pdg = new RdduFacade(_sootCfg).nodesMap();

    for(SootNode node : _pdg.values())
    {
      for(String use : node.getUseSet())
      {
        SootNode source = _pdg.get(node.getInSet().get(use));
        source.addTransition(node._lineNumber, use);
      }

      //TODO add transitions from CDG
    }
  }
  // </editor-fold> COMPUTATION ************************************************
}
