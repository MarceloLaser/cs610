package laser.cs610.projectbenchmark;

import java.util.Scanner;

public class ConcludePurchaseMenu
  extends ConsoleMenu
{
  public ConcludePurchaseMenu(Scanner sc)
  {
    super(sc, null);
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = 0;
    // error here. the error message shown is unspecific, dumb users won't be
    // able to identify the problem
    while(choice < 1 || choice > 2)
      choice = getMenuOption();

    if(choice == 2)
      return new MainMenu(_sc);

    System.out.println("Thank you for your purchase!");
    return null;
  }

  protected String menuText()
  {
    String result = "Would you like to conclude your purchase?";
    result += System.lineSeparator();
    result += "(1). Yes" + System.lineSeparator();
    result += "(2). No" + System.lineSeparator();
    return result;
  }
}
