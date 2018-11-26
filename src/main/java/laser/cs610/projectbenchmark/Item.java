package laser.cs610.projectbenchmark;

public class Item
{
  public final String _name;
  public final String _description;
  public final String _department;
  private int _quantity;

  public Item(String name, String department, String description)
  {
    _name = name;
    _department = department;
    _description = description;
    _quantity = 0;
  }

  public Item(String name, String department)
  {
    this(name, department, "no description");
  }

  public int add(int quantity)
  {
    return _quantity += quantity;
  }

  public int add()
  {
    return add(1);
  }

  public int remove(int quantity)
  {
    // error will occur if quantity > _quantity
    // this is intentional
    return _quantity -= quantity;
  }

  public int remove()
  {
    return remove(1);
  }

  public int getQuantity()
  {
    return _quantity;
  }
}
