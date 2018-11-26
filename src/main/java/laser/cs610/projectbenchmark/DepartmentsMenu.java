package laser.cs610.projectbenchmark;

import java.util.Map;
import java.util.Collection;
import java.util.Scanner;

public class DepartmentsMenu
  extends ConsoleMenu
{
  Collection<String> _departments;

  public DepartmentsMenu(Scanner sc, ConsoleMenu previousMenu)
  {
    super(sc, previousMenu);
    _departments = ConsoleShop._stock.getDepartments();
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = 0;
    // error here. the error message shown is unspecific, dumb users won't be
    // able to identify the problem
    // also, there is no option to return to main menu
    while(choice < 1 || choice > _departments.size())
      choice = getMenuOption();

    Map<String, Item> result =
      ConsoleShop._stock.getAllFromDepartment(
        (String)_departments.toArray()[choice]);
    return new SearchResultsMenu(_sc, this, result);
  }

  protected String menuText()
  {
    String result = "Please choose a department:" + System.lineSeparator();
    int option = 1;

    for(String dep : _departments)
      result += "(" + option++ + "). " + dep + System.lineSeparator();

    return result;
  }
}
