package laser.cs610.projectbenchmark;

import java.util.Scanner;
import laser.util.ConsoleMenu;

public class PBMainMenu
  implements ConsoleMenu
{
  private Scanner sc;

  public PBMainMenu()
  {
    sc = new Scanner(System.in);
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = getMenuOption();

    switch(choice)
    {
      case 1:
        return new PBSearchMenu();

      case 2:
        return new PBDepartmentsMenu();

      case 3:
        return new PBCheckCartMenu();

      case 4:
        return new PBShopInfoScreen();

      case 5:
        return new PBConcludePurchaseMenu();

      case 6:
        return null;

      default:
        throw new IllegalArgumentException();
    }
  }

  private String menuText()
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

  private Integer getMenuOption()
  {
    Boolean gotInput = false;
    Integer choice = 0;

    while(!gotInput)
    {
      try
      {
        choice = sc.nextInt();
        // if the next lines fail, program will malfunction.
        // this is intentional.
        if(choice > 0 && choice < 7)
          gotInput = true;
      }
      catch(Exception e)
      {
        System.out.println("Please enter a number between 1-6");
      }
    }

    return choice;
  }
}
