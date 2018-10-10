package laser.datastructures.soot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SootAdvancedControlNode
{
  // <editor-fold> FIELDS ******************************************************
  private Set<SootNode> _dominatorSet;
  private Set<SootNode> _postDominatorSet;
  private Set<SootNode> _dominatedSet;
  private Set<SootNode> _postDominatedSet;
  private Set<SootNode> _dominanceFrontier;
  private Set<SootNode> _controlDependencies;
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public SootAdvancedControlNode()
  {
    _dominatorSet = new HashSet<SootNode>();
    _postDominatorSet = new HashSet<SootNode>();
    _dominatedSet = new HashSet<SootNode>();
    _postDominatedSet = new HashSet<SootNode>();
    _dominanceFrontier = new HashSet<SootNode>();
    _controlDependencies = new HashSet<SootNode>();
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> ACCESSORS ***************************************************
  public void addDominated(SootNode dominated)
  {
    _dominatedSet.add(dominated);
  }

  public void addPostDominated(SootNode postDominated)
  {
    _postDominatedSet.add(postDominated);
  }

  public void initializeDominators(Collection<SootNode> nodes,
    SootNode composite)
  {
    if(composite._lineNumber == -1)
      _dominatorSet.add(composite);
    else
      _dominatorSet.addAll(nodes);
  }

  public void initializePostDominators(Collection<SootNode> nodes,
    SootNode composite)
  {
    if(composite._lineNumber == -2)
      _postDominatorSet.add(composite);
    else
      _postDominatorSet.addAll(nodes);
  }

  public void forwardDominated(SootNode composite)
  {
    for(SootNode dominator : _dominatorSet)
      dominator._advancedControlFlow.addDominated(composite);
  }

  public void forwardPostDominated(SootNode composite)
  {
    for(SootNode postDominator : _postDominatorSet)
      postDominator._advancedControlFlow.addPostDominated(composite);
  }

  public Set<SootNode> getDominators()
  {
    return new HashSet<SootNode>(_dominatorSet);
  }

  public Set<SootNode> getPostDominators()
  {
    return new HashSet<SootNode>(_postDominatorSet);
  }

  public Set<SootNode> getDominated()
  {
    return new HashSet<SootNode>(_dominatedSet);
  }

  public Set<SootNode> getPostDominated()
  {
    return new HashSet<SootNode>(_postDominatedSet);
  }

  public Set<SootNode> getDominanceFrontier()
  {
    return new HashSet<SootNode>(_dominanceFrontier);
  }

  public Set<SootNode> getControlDependencies()
  {
    return new HashSet<SootNode>(_controlDependencies);
  }
  // </editor-fold> ACCESSORS **************************************************

  // <editor-fold> COMPUTATION *************************************************
  public boolean computeDominators(Collection<SootNode> nodes,
    SootNode composite)
  {
    if(composite._lineNumber == -1)
      return false;

    Set<SootNode> resultingNodes = new HashSet<SootNode>(nodes);
    Set<SootNode> oldDominators = new HashSet<SootNode>(_dominatorSet);
    Collection<SootNode> parents =
      composite._controlFlow.getParents().values();

    for(SootNode parent : parents)
      resultingNodes.retainAll(parent._advancedControlFlow.getDominators());
    _dominatorSet = new HashSet<SootNode>();
    _dominatorSet.add(composite);
    _dominatorSet.addAll(resultingNodes);
    return !_dominatorSet.equals(oldDominators);
  }

  public boolean computePostDominators(Collection<SootNode> nodes,
    SootNode composite)
  {
    if(composite._lineNumber == -2)
      return false;

    Set<SootNode> resultingNodes = new HashSet<SootNode>(nodes);
    Set<SootNode> oldPostDominators = new HashSet<SootNode>(_postDominatorSet);
    Collection<SootNode> successors =
      composite._controlFlow.getSuccessors().values();

    for(SootNode child : successors)
      resultingNodes.retainAll(child._advancedControlFlow.getPostDominators());
    _postDominatorSet = new HashSet<SootNode>();
    _postDominatorSet.add(composite);
    _postDominatorSet.addAll(resultingNodes);
    return !_postDominatorSet.equals(oldPostDominators);
  }

  public void computeDominanceFrontiers()
  {
    Collection<SootNode> successors;
    for(SootNode dominated : _dominatedSet)
    {
      successors = dominated._controlFlow.getSuccessors().values();
      for(SootNode nodeToCheck : successors)
        if(!_dominatedSet.contains(nodeToCheck))
          _dominanceFrontier.add(nodeToCheck);
    }
  }

  public void computeControlDependencies()
  {
    Collection<SootNode> parents;
    for(SootNode postDominated : _postDominatedSet)
    {
      parents = postDominated._controlFlow.getParents().values();
      for(SootNode nodeToCheck : parents)
        if(!_postDominatedSet.contains(nodeToCheck))
          _controlDependencies.add(nodeToCheck);
    }
  }
  // </editor-fold> COMPUTATION ************************************************
}
