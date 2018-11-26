package laser.cs610.projectbenchmark;

import java.util.Scanner;

public abstract class ConsoleMenu
{
  protected Scanner _sc;
  protected ConsoleMenu _previousMenu;
  public abstract ConsoleMenu execute();
  protected abstract String menuText();

  public ConsoleMenu(Scanner sc, ConsoleMenu previousMenu)
  {
    _sc = sc;
    _previousMenu = previousMenu;
  }

  protected Integer getMenuOption()
  {
    Boolean gotInput = false;
    Integer choice = 0;

    while(!gotInput)
    {
      try
      {
        choice = _sc.nextInt();
        gotInput = true;
      }
      catch(Exception e)
      {
        System.out.println("Please enter a number");
      }
    }

    return choice;
  }
}
