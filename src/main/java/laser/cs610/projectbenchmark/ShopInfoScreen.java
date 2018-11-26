package laser.cs610.projectbenchmark;

import java.util.Scanner;

public class ShopInfoScreen
  extends ConsoleMenu
{
  public ShopInfoScreen(Scanner sc)
  {
    super(sc, null);
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    // error here: only the Enter key works for proceeding
    // this is intentional
    try { System.in.read(); } catch(Exception e) {}

    return new MainMenu(_sc);
  }

  protected String menuText()
  {
    String result = "Welcome to ConsoleShop!";
    result += System.lineSeparator() + System.lineSeparator();
    result += "We have a bunch of cool stuff that we hope you'll like. ";
    result += "We recognize that it's kinda buggy, but we can assure you ";
    result += "that it was made to be that way. We hope you'll be able to ";
    result += "catch all our bugs by running those tests that the Program ";
    result += "Analysis whatchamacallit generates for you!";
    result += System.lineSeparator() + System.lineSeparator();
    result += "Press any key to go back to the main menu.";

    return result;
  }
}
