public class SymExTest
{
  public void method1(int a, int b)
  {
    int c = 0;
    a = 7;
    if(a < 5)
      c = 3;
    if(a > 5)
      c = 2;
    if(b == a)
    {
      if(b != a)
        c = 7;
      else
        c = c;
    }

    for(int i = 0; i < b; i++)
    {
      c = c + a;
      if(i > b)
        c = c + b;
    }
  }

  public void method2(int[] d)
  {
    int f;
    for(int e : d)
    {
      if(e > d.length)
        f = 13;
    }
  }

  public void method3(int a, int b)
  {
    int c = 0;
    if(a < 5)
      c = 3;
    if(a > 5)
      c = 2;
    if(b == a)
    {
      if(b != a)
        c = 7;
      else
        c = c;
    }

    for(int i = 0; i < b; i++)
    {
      c = c + a;
      if(i > b)
        c = c + b;
    }
  }
}
