import java.util.Vector;
import java.util.Collections;
import java.util.HashMap;

import java.rmi.server.RemoteServer;

import java.util.UUID;

public class SorterImplementation implements Sorter {
  private HashMap<UUID, Vector<Integer>> stackMap;
  
  // Initialise the hashmap with the key UUID and the stack that belongs
  // individually to each clients as the value.
  public SorterImplementation() {
    stackMap = new HashMap<UUID, Vector<Integer>>();
  }
  
  // This is a helper function that helps other functions get the appropriate
  // stack for their clients.
  private Vector<Integer> getClientStack(UUID uuid) {
    try {
    } catch (Exception e) {
      
    }
    if (!stackMap.containsKey(uuid)) {
      stackMap.put(uuid, new Vector<Integer>());
    }
    return stackMap.get(uuid);
  }
  
  // This function pushes the value val to the appropriate stack for each 
  // clients
  public void pushValue(int val, UUID uuid) {
    synchronized(this)
    {
      Vector<Integer> stack = getClientStack(uuid);
      stack.add(val);
    }
    return;
  }
  
  // This function does operations on the stack based on the operator
  // it receives. This function will operate on the correct unique stack for
  // each clients.
  public void pushOperator(String operator, UUID uuid) {
    Vector<Integer> stack = null;
    if (operator.equals("ascending")) {
      synchronized(this) {
        stack = getClientStack(uuid);
        Collections.sort(stack);
      }
    }
    else if (operator.equals("descending")) {
      synchronized (this) {
        stack = getClientStack(uuid);
        Collections.sort(stack, Collections.reverseOrder());
      }
    }
    else if(operator.equals("max")) {
      synchronized (this) {
        stack = getClientStack(uuid);
        Integer maxValue = Collections.max(stack);
        stack.clear();
        stack.add(maxValue);
      }
    }
    else if(operator.equals("min")) {
      synchronized (this) {
        stack = getClientStack(uuid);
        Integer minValue = Collections.min(stack);
        stack.clear();
        stack.add(minValue);
      }
    }
  }
  
  // This function pops the value off the correct stack foe each clients
  // then return that value back to the client requesting this operation.
  public int pop(UUID uuid) {
    Vector<Integer> stack = null;
    synchronized (this) {
      stack = getClientStack(uuid);
      if (!stack.isEmpty())
      {
        Integer lastElement = stack.lastElement();
        stack.remove(stack.size() - 1);
        return lastElement;
      }
      return Integer.MIN_VALUE;
    }
  }
  
  // This function checks whether or not the stack that belongs to the client
  // requesting it is empty.
  public boolean isEmpty(UUID uuid) {
    Vector<Integer> stack = null;
    synchronized (this) {
      stack = getClientStack(uuid);
      return stack.isEmpty();
    }
  }
  
  // This function waits a few moment (millis milliseoconds) then pops the value
  // off the client's stack  and return the result to the client requesting
  // this operatiom.
  public int delayPop(int millis, UUID uuid) {
    try {
      Thread.sleep(millis);
    }
    catch(InterruptedException ie)
    {
      
    }
    return pop(uuid);
  }
}