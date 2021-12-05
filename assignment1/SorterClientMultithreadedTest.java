import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Scanner;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import java.io.File;

// This class does single-threaded testing for the RMI
public class SorterClientMultithreadedTest extends Thread{
  public String fileLocation;
  public Integer threadNumber;
  
  // This is the actual function that does the testing.
  // There will be multiople copies of this object that will have this run
  // function executed asynchroniously for each threads.
  public void run()
  {
    UUID uuid = UUID.randomUUID();
    String hostname = null;
    try {
      Registry registry = LocateRegistry.getRegistry(hostname);
      Sorter stub = (Sorter) registry.lookup("Sorter");

      System.out.println("Testing for thread # " + 
                         threadNumber.toString() + "begin");
      
      File file = new File(fileLocation);
      
      Scanner sc = new Scanner(file);
      int lastPopped = 0;
      boolean isEmpty = true;
      while (sc.hasNextLine()) {
        String input = sc.nextLine();
        String[] splitInput = input.split("\\s+");
        if (splitInput[0].equals("pv")) {
          stub.pushValue(Integer.parseInt(splitInput[1]), uuid);
          System.out.println("Pushed value: " + splitInput[1]);
        } else if (splitInput[0].equals("po")) {
          stub.pushOperator(splitInput[1], uuid);
          System.out.println("Pushed operator: " + splitInput[1]);
        } else if (splitInput[0].equals("pop")) {
          lastPopped = stub.pop(uuid);
          System.out.println("Popped value: " + lastPopped);
        } else if (splitInput[0].equals("ie")) {
          isEmpty = stub.isEmpty(uuid);
          System.out.println("Is empty: " + isEmpty);
          System.out.println("Expected: " + splitInput[1]);
          if ((splitInput[1].equals("true") && !isEmpty) || 
              (splitInput[1].equals("false") && isEmpty)) {
            System.err.println("ERROR: Output Mismatch!");
            return;
          }
        } else if (splitInput[0].equals("cv")) {
          if (lastPopped != Integer.parseInt(splitInput[1])) {
            System.err.println("ERROR: Output mismatch");
            System.err.println("Expected: " + splitInput[1]);
            System.err.println("Got: " + lastPopped);
            return;
          }
        } else if (splitInput[0].equals("printv")) {
          System.out.println(lastPopped);
        } else if (splitInput[0].equals("pcv")) {
          lastPopped = stub.pop(uuid);
          System.out.println("Expected: " + splitInput[1] + 
                             " - Popped: " + lastPopped);
          if (lastPopped != Integer.parseInt(splitInput[1])) {
            System.err.println("ERROR: Output mismatch");
            return;
          }
        } else if (splitInput[0].equals("wpopcv")) {
          Integer targetMillis = Integer.parseInt(splitInput[2]);
          Long firstTick = System.currentTimeMillis();
          lastPopped = stub.delayPop(targetMillis, uuid);
          Long secondTick = System.currentTimeMillis();
          Long elapsedtime = secondTick - firstTick;
          Double tolerance = Double.parseDouble(splitInput[3]);
          System.out.println("Expected: " + splitInput[1] + 
                             " - Popped: " + lastPopped);
          Double lowerTolerance = (1.0 - tolerance) * targetMillis;
          Double upperTolerance = (1.0 + tolerance) * targetMillis;
          System.out.println("Elapsed time since call: " + 
                             elapsedtime.toString() + "ms");
          System.out.println("Tolerance Window: " + lowerTolerance.toString() + 
                             "... " + upperTolerance.toString());
          if (lastPopped != Integer.parseInt(splitInput[1])) {
            System.err.println("ERROR: Output mismatch");
            return;
          }
          if (elapsedtime <  lowerTolerance ||
              elapsedtime > upperTolerance)
          {
            System.err.println("ERROR: Timeout");
            return;
            
          }

        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Test for Thread #" + threadNumber.toString() +
                        " Succeeded!");
  }
  
  // The main function of this SorterClient class
  // This main function reads custom testing commands fed in from a text file
  // and does the appropriate actions to test.
  public static void main(String[] args) {
    Vector<SorterClientMultithreadedTest> threadVector =  new Vector<>();
    int threadCtr = 0;
    for (String str : args)
    {
      // Create new threads and specify their threadNumber and test file
      // location.
      threadVector.add(new SorterClientMultithreadedTest());
      threadVector.lastElement().fileLocation = str;
      threadVector.lastElement().threadNumber = threadCtr;
      threadCtr++;
    }
    for (SorterClientMultithreadedTest scmt : threadVector) {
      scmt.start();
    }
  }
}