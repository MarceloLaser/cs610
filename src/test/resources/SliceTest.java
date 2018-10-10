public class SliceTest
{
  public static void main(String args[])
  {
    int a = Integer.parseInt(args[0]);
    if(Integer.parseInt(args[1]) > 0)
      for(int i = 0; i < Integer.parseInt(args[1]); i++)
        System.out.print(i + ":" + a);
    else
      System.out.print(Integer.parseInt(args[1]) + "is less than zero.");
  }
}
