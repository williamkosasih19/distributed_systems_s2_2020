import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.lang3.mutable.MutableInt;

// This is the AggregateServer class.
// This component is responsible for aggregating the feeds from the ContentServers
// together, and serving request from the Clients when they ask for feeds.
// This component contains several sub-components:
// 1. WorkerThread: This thread is responsible for handling requests from both the
//    clients and the content servers. For clients, this thread will return a message containing the feeds in ATOM
//    format. Whereas for handling the requests from the ContentServers, this thread will add an entry
//    to the feed hashmap which maps the InetAdressPortPair unique to each connection to each of their feed.
// 2. HouseKeeperThread: This component is responsible for checking if each feeds is still valid,
//    this thread will send a heartbeat message to ecah of the ContentServer's ping port every 12 seconds.
//    If the ContentServer is able to repsond appropriately to the heartbeet request, the the feed will be kept.
//    Otherwise, it will be removed from the feed map.
// 3. SaveThread: This component, will export the value of each feeds in the map every s seconds, and put them into a text file
//    along with the essential informations such as the address of the ContentServer, the heartbeat port, and the message length.
public class AggregateServer {
  private static HashMap<InetAddressPortPair, String> feedDataMap;
  private static MutableInt lamportClock;
  Integer portNumber;
    
  public static void main(String[] args) {
    Integer portNumber = Constants.AGGREGATE_SERVER_PORT_NUMBER;
    if (args.length > 0) {
      Integer.parseInt(args[0]);
    }
    lamportClock = new MutableInt();
    lamportClock.setValue(0);
    
    ServerSocket serverSocket = null;
  
    try {
          serverSocket = new ServerSocket(portNumber);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    feedDataMap = new HashMap<>();
    
    // If backup.log exists, then try to restore the feeds.
    if (new File("backup.log").exists()) {
      Scanner scanner = null;
      try {
        scanner = new Scanner(new File("backup.log"));

      } catch (Exception exception) {
        exception.printStackTrace();
      }
        while (scanner.hasNextLine()) {
          InetAddress contentServerAddress = null;
          String header = scanner.nextLine();
          if (header.equals(Constants.TERMINATE_SEQUENCE))
            break;
          try {
            contentServerAddress = InetAddress.getByName(header);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
          Integer contentServerPort = Integer.parseInt(scanner.nextLine());
          Integer pingServerPort = Integer.parseInt(scanner.nextLine());
          
          Integer linesToRead = Integer.parseInt(scanner.nextLine());
          String message = "";
          for (Integer i = 0; i < linesToRead; i++) {
            message += scanner.nextLine() + "\r\n";
          }
          // Put each feed to the hasmap
          feedDataMap.put(new InetAddressPortPair(contentServerAddress, contentServerPort, 
              pingServerPort, linesToRead), message);
          System.out.println("feed from: " + contentServerAddress.getHostAddress() + ":" + contentServerPort + " has been restored!");
      }
    }
    // The house keeper thread will immediately try to ping each feed's ping port once it's put in the hasmap.
    // This means that every feed will be validated once they're restored from backup.log
    AggregateServer_HouseKeeperThread houseKeeperThread = new AggregateServer_HouseKeeperThread(feedDataMap, lamportClock);
    houseKeeperThread.start();
    
    // SaveThread works independently of other threads, and is made sure to stay alive even when other threads stops functioning
    // ensuring the integrity of the backup log.
    AggregateServer_SaveThread saveThread = new AggregateServer_SaveThread(feedDataMap, lamportClock);
    saveThread.start();
    
    // Keep on listening on the serverSocket, and create a new WorkerThread whenever a new request is picked up.
    while (true) {
      try {
        Socket clientSocket = serverSocket.accept();
        
        Thread workerThread = new AggregateServer_WorkerThread(feedDataMap, clientSocket, lamportClock);
        workerThread.start();
        
      } catch (Exception e) {
        e.printStackTrace();
      }      
    }
  }
  
}