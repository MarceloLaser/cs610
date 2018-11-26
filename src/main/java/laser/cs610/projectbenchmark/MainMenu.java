package laser.cs610.projectbenchmark;

import java.util.Scanner;

public class MainMenu
  extends ConsoleMenu
{
  public MainMenu(Scanner sc)
  {
    super(sc, null);
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = 0;
    // error here. the error message shown is unspecific, dumb users won't be
    // able to identify the problem
    while(choice < 1 || choice > 6)
      choice = getMenuOption();

    switch(choice)
    {
      case 1:
        return new SearchMenu(_sc, this);

      case 2:
        return new DepartmentsMenu(_sc, this);

      case 3:
        return new SearchResultsMenu(_sc, this,
          ConsoleShop._cart.getAllItems());

      case 4:
        return new ShopInfoScreen(_sc);

      case 5:
        return new ConcludePurchaseMenu(_sc);

      case 6:
        return null;

      default:
        throw new IllegalArgumentException();
    }
  }

  protected String menuText()
  {
    String result = "Please choose an option from the menu:"
      + System.lineSeparator();
    result += "(1). Search shop" + System.lineSeparator();
    result += "(2). Departments" + System.lineSeparator();
    result += "(3). Check cart" + System.lineSeparator();
    result += "(4). Shop information" + System.lineSeparator();
    result += "(5). Conclude purchase" + System.lineSeparator();
    result += "(6). Exit shop" + System.lineSeparator();

    return result;
  }


}
