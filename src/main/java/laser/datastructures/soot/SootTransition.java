package laser.datastructures.soot;

public class SootTransition
{
  public final int _targetLineNumber;
  public final String _transitionLabel;

  public SootTransition(int targetLineNumber, String transitionLabel)
  {
    _targetLineNumber = targetLineNumber;
    if(transitionLabel != null)
      _transitionLabel = transitionLabel;
    else
      _transitionLabel = "";
  }

  @Override
  public boolean equals(Object o)
  {
    if(o == this)
      return true;
    if(!(o instanceof SootTransition))
      return false;
    SootTransition toCompare = (SootTransition)o;

    if(this._targetLineNumber == toCompare._targetLineNumber
      && this._transitionLabel.equals(toCompare._transitionLabel))
      return true;
    return false;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + _targetLineNumber;
    result = prime * result
      + ((_transitionLabel == null) ? 0 : _transitionLabel.hashCode());
    return result;
  }
}
