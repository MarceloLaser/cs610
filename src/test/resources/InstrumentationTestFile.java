public class InstrumentationTestFile
{
  public static void main(String args[])
  {
    StringBuffer failString = "";
    System.out.println("Starting up Instrumentation Test");

    failString = whileTest(failString);
    failString = doWhileTest(failString);
    failString = forTest(failString);
    failString = forEachTest(failString);

    System.out.println("Concluding Instrumentation Test");
    System.out.println(failString);
  }

  private static StringBuffer whileTest(StringBuffer failString)
  {
    int whileNorm = 0;

    System.out.println("Testing [While] normal execution");
    while(whileNorm < 5)
    {
      whileNorm++;
      String state = "[While] normal execution: iteration " + whileNorm;
      System.out.println(state);
      failString.append("Failure at " + state + System.lineSeparator());
    }

    whileNorm = 0;
    System.out.println("Testing [While] empty execution");
    while(whileNorm++ < 5);

    System.out.println("Testing [While] no execution");
    while(whileNorm < 5)
    {
      failString.append("Failure at [While] no execution");
      failString.append(System.lineSeparator());
    }

    return failString;
  }

  private static StringBuffer doWhileTest(StringBuffer failString)
  {
    int doWhileNorm = 0;

    System.out.println("Testing [DoWhile] normal execution");
    do
    {
      String state = "[DoWhile] normal execution: iteration " + doWhileNorm;
      System.out.println(state);
      failString.append("Failure at " + state + System.lineSeparator());
    } while (++doWhileNorm < 5);

    doWhileNorm = 0;
    System.out.println("Testing [DoWhile] empty execution");
    do {} while (++doWhileNorm < 5);

    System.out.println("Testing [DoWhile] one execution");
    do
    {
      failString.append("Failure at [DoWhile] no execution");
      failString.append(System.lineSeparator());
    } while (doWhileNorm < 5);

    return failString;
  }

  private static StringBuffer forTest(StringBuffer failString)
  {
    System.out.println("Testing [For] normal execution");
    for (int i = 0; i < 5; i++)
    {
      String state = "[For] normal execution: iteration " + i;
      System.out.println(state);
      failString.append("Failure at " + state + System.lineSeparator());
    }

    System.out.println("Testing [For] empty execution");
    for (int i = 0; i < 5; i++);

    System.out.println("Testing [For] no execution");
    for (int i = 0; i < 0; i++)
    {
      failString.append("Failure at [For] no execution");
      failString.append(System.lineSeparator());
    }

    return failString;
  }

  private static StringBuffer forEachTest(StringBuffer failString)
  {
    int[] vector = { 0, 1, 2, 3, 4 };
    int[] emptyVector = { };

    System.out.println("Testing [ForEach] normal execution");
    for (int i : vector)
    {
      String state = "[ForEach] normal execution: iteration " + (i+1);
      System.out.println(state);
      failString.append("Failure at " + state + System.lineSeparator());
    }

    System.out.println("Testing [ForEach] empty execution");
    for (int i : vector);

    System.out.println("Testing [ForEach] no execution");
    for (int i : emptyVector)
    {
      failString.append("Failure at [ForEach] no execution");
      failString.append(System.lineSeparator());
    }

    return failString;
  }
}
