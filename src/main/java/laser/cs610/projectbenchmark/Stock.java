package laser.cs610.projectbenchmark;

import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Stock
{
  private Map<String, Item> _items;

  public Stock()
  {
    _items = new HashMap<String, Item>();
  }

  public void addItem(Item item)
  {
    _items.put(item._name, item);
  }

  public void removeItem(Item item)
  {
    removeItem(item._name);
  }

  public void removeItem(String item)
  {
    _items.remove(item);
  }

  public void empty(Stock recipient)
  {
    // error happens if sendTo is null
    // this is intentional
    for(Item item : _items.values())
      moveItem(item, recipient);
  }

  public Map<String, Item> getAllFromDepartment(String department)
  {
    Map<String, Item> toReturn = new HashMap<String, Item>();
    for(Item item : _items.values())
      if(item._department.equals(department))
        toReturn.put(item._name, item);
    return toReturn;
  }

  public void moveItem(Item item, Stock recipient)
  {
    // error happens here: since quantity is held in item, the entire
    // stock of that item is transfered.
    recipient.addItem(item);
    removeItem(item);
  }

  public Collection<String> getDepartments()
  {
    Collection<String> toReturn = new HashSet<String>();
    for(Item item : _items.values())
      toReturn.add(item._department);

    return toReturn;
  }

  public Map<String, Item> searchByString(String searchTerm)
  {
    Map<String, Item> toReturn = new HashMap<String, Item>();
    for(Item item : _items.values())
      if(item._name.contains(searchTerm))
        toReturn.put(item._name, item);
    return toReturn;
  }

  public Map<String, Item> getAllItems()
  {
    return _items;
  }
}
