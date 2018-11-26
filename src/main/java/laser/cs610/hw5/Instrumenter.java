package laser.cs610.hw5;

import soot.jimple.StringConstant;
import soot.jimple.ReturnVoidStmt;
import laser.util.CompilerDirectives;
import java.io.IOException;
import java.io.OutputStreamWriter;
import soot.util.JasminOutputStream;
import java.io.FileOutputStream;
import soot.jimple.JasminClass;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.File;
import soot.jimple.InvokeStmt;
import soot.Scene;
import soot.SootMethod;
import soot.RefType;
import soot.Unit;
import java.util.Iterator;
import soot.jimple.LongConstant;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import java.util.Collection;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.LongType;
import soot.jimple.Jimple;
import soot.Local;
import laser.cs610.SootExtension;

public class Instrumenter
  extends SootExtension
{
  // <editor-fold> FIELDS ******************************************************
  private String _outputDirectory;
  private static String _usageInstructions;
  private static final String _totalBlaBla = "Total number of iterations is: ";
  // </editor-fold> FIELDS *****************************************************

  // <editor-fold> INITIALIZATION **********************************************
  public Instrumenter(String sootClassPathAppend, String className,
    String outputDirectory, String methodName)
  {
    super(className, sootClassPathAppend, methodName);
    _outputDirectory = outputDirectory;
  }

  public Instrumenter(String sootClassPathAppend, String className,
    String outputDirectory)
  {
    this(sootClassPathAppend, className, outputDirectory, "main");
  }
  // </editor-fold> INITIALIZATION *********************************************

  // <editor-fold> COMPUTATION *************************************************
  private Local createCounter()
  {
		Local counterLocal = Jimple.v().newLocal("counterLocal", LongType.v());
		_b.getLocals().add(counterLocal);
    AssignStmt declaration = Jimple.v().newAssignStmt(
      counterLocal, LongConstant.v(0));
    _units.insertAfter(declaration, _units.getFirst());

    return counterLocal;
  }

  private void insertIncrement(Local counterLocal, Loop lp)
  {
    Collection<Stmt> lpExits = lp.getLoopExits();
    IfStmt exit = null;
    for(Stmt st : lpExits)
      if(st instanceof IfStmt)
        exit = (IfStmt)st;
    if(exit == null)
      throw new IllegalArgumentException("Well, looks like that didn't work.");
    AddExpr newCounter = Jimple.v().newAddExpr(counterLocal, LongConstant.v(1));
    AssignStmt increment = Jimple.v().newAssignStmt(counterLocal, newCounter);
    _units.insertAfter(increment, exit);
  }

  private void insertReturnPrints(Local counterLocal)
  {
    Iterator<Unit> it = _units.snapshotIterator();

    while(it.hasNext())
    {
      Stmt current = (Stmt)it.next();
      if(current instanceof ReturnVoidStmt)
      {
        Local printer =
          Jimple.v().newLocal("printer", RefType.v("java.io.PrintStream"));
				_b.getLocals().add(printer);

				AssignStmt printerAsg =
          Jimple.v().newAssignStmt(printer, Jimple.v().newStaticFieldRef(
            Scene.v().getField(
              "<java.lang.System: java.io.PrintStream out>").makeRef()));
				_units.insertBefore(printerAsg, current);

				SootMethod callPrintLong =
          Scene.v().getMethod("<java.io.PrintStream: void print(long)>");

        SootMethod callPrintString =
          Scene.v().getMethod(
            "<java.io.PrintStream: void print(java.lang.String)>");

				InvokeStmt printCount1 =
          Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
            printer, callPrintString.makeRef(), buildReport(_totalBlaBla)));
        InvokeStmt printCount2 =
          Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
            printer, callPrintLong.makeRef(), counterLocal));
        InvokeStmt printCount3 =
          Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
            printer, callPrintString.makeRef(),
              buildReport(System.lineSeparator())));
				_units.insertAfter(printCount1, printerAsg);
        _units.insertAfter(printCount2, printCount1);
        _units.insertAfter(printCount3, printCount2);
      }
    }
  }

  private void replaceAppends(Local counterLocal)
  {
    Iterator<Unit> it = _units.snapshotIterator();

    while(it.hasNext())
    {
      Stmt current = (Stmt)it.next();
      if(current instanceof InvokeStmt)
      {
        SootMethod currentMethod = current.getInvokeExpr().getMethod();
        String declaringClass = currentMethod.getDeclaringClass().getName();
        String methodName = currentMethod.getName();

        if(declaringClass.equals("java.lang.StringBuffer")
          && methodName.equals("append"))
        {
          Local printer =
            Jimple.v().newLocal("printer", RefType.v("java.io.PrintStream"));
  				_b.getLocals().add(printer);

  				AssignStmt printerAsg =
            Jimple.v().newAssignStmt(printer, Jimple.v().newStaticFieldRef(
              Scene.v().getField(
                "<java.lang.System: java.io.PrintStream out>").makeRef()));
  				_units.swapWith(current, printerAsg);

  				SootMethod callPrintLong =
            Scene.v().getMethod("<java.io.PrintStream: void print(long)>");

          SootMethod callPrintString =
            Scene.v().getMethod(
              "<java.io.PrintStream: void print(java.lang.String)>");

  				InvokeStmt printCount1 =
            Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
              printer, callPrintString.makeRef(), buildReport(_totalBlaBla)));
          InvokeStmt printCount2 =
            Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
              printer, callPrintLong.makeRef(), counterLocal));
          InvokeStmt printCount3 =
            Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
              printer, callPrintString.makeRef(),
                buildReport(System.lineSeparator())));
  				_units.insertAfter(printCount1, printerAsg);
          _units.insertAfter(printCount2, printCount1);
          _units.insertAfter(printCount3, printCount2);
        }
      }
    }
  }

  private StringConstant buildReport(String s)
  {
    return StringConstant.v(s);
  }
  // </editor-fold> COMPUTATION ************************************************

  // <editor-fold> IO OPERATIONS ***********************************************
  public static void main(String args[])
    throws IOException
  {
    Instrumenter inst = unpackArguments(args);

    Local counterLocal = inst.createCounter();

    for(Loop lp : inst._loops)
      inst.insertIncrement(counterLocal, lp);

    inst.insertReturnPrints(counterLocal);
    inst.replaceAppends(counterLocal);

    inst._b.validate();
    inst.printResult();
  }

  public void printResult()
    throws IOException
  {
    String filename = _outputDirectory + File.separator + _className + ".class";
		File fn = new File(filename);
		fn.getParentFile().mkdirs();
		OutputStream streamOut = new JasminOutputStream(new FileOutputStream(fn));
		PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
		JasminClass jasminClass = new JasminClass(_sc);
		jasminClass.print(writerOut);
		writerOut.flush();
		streamOut.close();

    if(CompilerDirectives.DEBUG)
    {
      for(Unit u : _units)
      {
        logUnit(u);
      }
    }
  }

  private static Instrumenter unpackArguments(String[] args)
  {
    _usageInstructions = "Usage: java -jar cs610-Instrumenter.jar ";
    _usageInstructions += "<SootClassPath> <ClassToAnalyzeName>";
    _usageInstructions += "<OutputDirectory> [MethodName]";

    if(args.length < 3)
    {
      System.out.println(_usageInstructions);
      System.exit(1);
    }

    String sootClassPathAppend = args[0];
    String className = args[1];
    String outputDirectory = args[2];
    if(args.length > 3)
      return new Instrumenter(sootClassPathAppend, className,
        outputDirectory, args[3]);
    return new Instrumenter(sootClassPathAppend, className, outputDirectory);
  }
  // </editor-fold> IO OPERATIONS **********************************************
}
