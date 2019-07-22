package laser.cs610.projectbenchmark;

import java.util.Scanner;

public class ViewItemMenu
  extends ConsoleMenu
{
  Item _itemView;

  public ViewItemMenu(Scanner sc, ConsoleMenu previousMenu, Item item)
  {
    super(sc, previousMenu);
    _itemView = item;
  }

  public ConsoleMenu execute()
  {
    System.out.print(menuText());
    Integer choice = 0;
    // error here. the error message shown is unspecific, dumb users won't be
    // able to identify the problem. also should not allow options 4 and 5
    // when appropriate.
    while(choice < 1 || choice > 5)
      choice = getMenuOption();

    if(choice == 1)
      return _previousMenu;

    if(choice == 2)
      return new MainMenu(_sc);

    // error here. the cause is in Stock (due to quantity being held by item).
    if(choice == 3)
      ConsoleShop._stock.moveItem(_itemView, ConsoleShop._cart);

    // same as above
    if(choice == 4)
      ConsoleShop._cart.moveItem(_itemView, ConsoleShop._stock);

    // this one is just blatantly derpy, but it's to make a point.
    if(choice == 5)
      ConsoleShop._cart.moveItem(_itemView, ConsoleShop._stock);

    // this would theoretically return to the itemView screen, but will fail
    // and possibly cause a fatal error because of the errors in 3, 4 and 5.
    return this;
  }

  protected String menuText()
  {
    String result = _itemView._name + ":" + System.lineSeparator();
    result += System.lineSeparator();
    result += _itemView._description + System.lineSeparator();
    result += System.lineSeparator();
    result += _itemView.getQuantity() + " left in stock";
    result += System.lineSeparator();
    result += "(1). Return to previous menu" + System.lineSeparator();
    result += "(2). Return to main menu" + System.lineSeparator();
    result += "(3). Add item to cart" + System.lineSeparator();
    // error here. the next two options should only show if the item is in cart
    result += "(4). Remove one" + _itemView._name + "from cart";
    result += System.lineSeparator();
    result += "(5). Remove all" + _itemView._name + "from cart";
    result += System.lineSeparator();

    return result;
  }
}
