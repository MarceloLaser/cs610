package laser.datastructures;

public class Triple<E,F,G>
{
  private E _item1;
  private F _item2;
  private G _item3;

  public Triple(E item1, F item2, G item3)
  {
    _item1 = item1;
    _item2 = item2;
    _item3 = item3;
  }

  public E getItem1()
  {
    return _item1;
  }

  public F getItem2()
  {
    return _item2;
  }

  public G getItem3()
  {
    return _item3;
  }

  @Override
  public boolean equals(Object o)
  {
    if(o == this)
      return true;
    if(!(o instanceof Triple))
      return false;
    Triple<E,F,G> toCompare = (Triple<E,F,G>)o;
    boolean item3isEqual = true;
    if(this._item3 == null && toCompare.getItem3() == null)
      item3isEqual = true;
    if((this._item3 == null && toCompare.getItem3() != null)
      || this._item3 != null && toCompare.getItem3() == null)
      item3isEqual = false;
    if(this._item3 != null && toCompare.getItem3() != null)
      item3isEqual = this._item3.equals(toCompare.getItem3());

    if(this._item1.equals(toCompare.getItem1())
      && this._item2.equals(toCompare.getItem2())
      && item3isEqual)
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
    result = prime * result + ((_item3 == null) ? 0 : _item3.hashCode());
    return result;
  }
}
