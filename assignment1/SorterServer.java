import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class SorterServer {
  public SorterServer() {
    
  }
  
  public static void main(String args[]) {
    try {
      SorterImplementation obj = new SorterImplementation();
      Sorter stub = (Sorter) UnicastRemoteObject.exportObject(obj, 0);
      
      Registry registry = LocateRegistry.getRegistry();
      registry.bind("Sorter", stub);
      
      System.err.println("Server ready");
    }
    catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();  
    }
  }
}