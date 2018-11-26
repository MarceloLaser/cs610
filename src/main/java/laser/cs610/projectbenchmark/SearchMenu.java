package laser.cs610.projectbenchmark;

import java.util.Map;
import java.util.Scanner;

public class SearchMenu
  extends ConsoleMenu
{
  public SearchMenu(Scanner sc, ConsoleMenu previousMenu)
  {
    super(sc, previousMenu);
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    String searchTerm = _sc.next();

    Map<String, Item> result = ConsoleShop._stock.searchByString(searchTerm);
    return new SearchResultsMenu(_sc, this, result);
  }

  protected String menuText()
  {
    String result = "Enter a search string:" + System.lineSeparator();
    result += "(Do not use spaces or special characters)";
    result += System.lineSeparator();
    return result;
  }
}
