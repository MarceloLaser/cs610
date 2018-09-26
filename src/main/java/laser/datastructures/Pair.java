package laser.datastructures;

public class Pair<E,F>
{
  private E _item1;
  private F _item2;

  public Pair(E item1, F item2)
  {
    _item1 = item1;
    _item2 = item2;
  }

  public E getItem1()
  {
    return _item1;
  }

  public F getItem2()
  {
    return _item2;
  }

  @Override
  public boolean equals(Object o)
  {
    if(o == this)
      return true;
    if(!(o instanceof Pair))
      return false;
    Pair<E,F> toCompare = (Pair<E,F>)o;

    if(this._item1.equals(toCompare.getItem1())
      && this._item2.equals(toCompare.getItem2()))
      return true;
    return false;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_item1 == null) ? 0 : _item1.hashCode());
    result = prime * result + ((_item2 == null) ? 0 : _item2.hashCode());
    return result;
  }
}
