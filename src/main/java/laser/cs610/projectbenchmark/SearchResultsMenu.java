package laser.cs610.projectbenchmark;

import java.util.Map;
import java.util.Scanner;

public class SearchResultsMenu
  extends ConsoleMenu
{
  Map<String, Item> _searchResult;

  public SearchResultsMenu(Scanner sc, ConsoleMenu previousMenu,
    Map<String, Item> searchResult)
  {
    super(sc, previousMenu);
    _searchResult = searchResult;
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = 0;
    // error here. the error message shown is unspecific, dumb users won't be
    // able to identify the problem
    while(choice < 1 || choice > _searchResult.size()+2)
      choice = getMenuOption();

    if(choice == _searchResult.size()+1)
      return _previousMenu;

    if(choice == _searchResult.size()+2)
      return new MainMenu(_sc);

    Item result = (Item)_searchResult.values().toArray()[choice];
    return new ViewItemMenu(_sc, this, result);
  }

  protected String menuText()
  {
    String result = "Please choose an item to view:" + System.lineSeparator();
    int option = 1;

    for(String item : _searchResult.keySet())
      result += "(" + option++ + "). " + item + System.lineSeparator();

    result += "(" + option++ + "). Return to previous menu"
      + System.lineSeparator();
    result += "(" + option++ + "). Return to main menu"
      + System.lineSeparator();

    return result;
  }
}
