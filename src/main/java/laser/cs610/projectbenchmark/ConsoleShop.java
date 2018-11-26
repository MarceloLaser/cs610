package laser.cs610.projectbenchmark;

import java.util.Scanner;

public class ConsoleShop
{
  public static Stock _stock;
  public static Stock _cart;

  public static void main(String args[])
  {
    _stock = ShopInitializer.makeStock();
    _cart = new Stock();
    Scanner sc = new Scanner(System.in);

    ConsoleMenu menu = new MainMenu(sc);
    while(menu != null)
    {
      menu = menu.execute();
    }

    System.out.println("Come again!");
  }
}
