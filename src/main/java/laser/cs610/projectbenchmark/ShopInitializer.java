package laser.cs610.projectbenchmark;

public class ShopInitializer
{
  public static Stock makeStock()
  {
    Stock stock = new Stock();

    Item milk = new Item("Milk", "It's milk.", "Food");
    milk.add(15);
    stock.addItem(milk);
    Item butter = new Item("Butter", "I can believe it's butter.", "Food");
    butter.add(12);
    stock.addItem(butter);
    Item water = new Item("Water", "Who buys this anyway.", "Food");
    water.add(30);
    stock.addItem(water);
    Item shirt = new Item("Shirt", "You wear these.", "Clothes");
    shirt.add(5);
    stock.addItem(shirt);
    Item computerScreen = new Item ("Computer Screen",
      "Handy for seeing what you're doing.", "Electronics");
    computerScreen.add(2);
    stock.addItem(computerScreen);
    Item keyboard = new Item("Keyboard",
      "Essential for getting carpal tunnel syndrome.", "Electronics");
    keyboard.add(3);
    stock.addItem(keyboard);

    return stock;
  }
}
