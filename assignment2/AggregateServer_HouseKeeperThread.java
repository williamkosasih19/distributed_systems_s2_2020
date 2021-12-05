import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.mutable.MutableInt;

// This is the HouseKeeperThread class.
// This component is responsible for checking if each feeds is still valid,
// this thread will send a heartbeat message to ecah of the ContentServer's ping port every 12 seconds.
// If the ContentServer is able to repsond appropriately to the heartbeet request, the the feed will be kept.
// Otherwise, it will be removed from the feed map.
public class AggregateServer_HouseKeeperThread extends Thread {
  
  private HashMap<InetAddressPortPair, String> feedDataMap;
  private MutableInt lamportClock;
  
  public AggregateServer_HouseKeeperThread(HashMap<InetAddressPortPair, String> feedDataMap,
                                           MutableInt lamportClock) {
    this.feedDataMap = feedDataMap;
    this.lamportClock = lamportClock;
  }
  
  public void run() {
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CODE) {
        Iterator iterator = feedDataMap.entrySet().iterator();
        while (iterator.hasNext()) {
          HashMap.Entry pair = (HashMap.Entry) iterator.next();
          InetAddressPortPair inetAddressPortPair = (InetAddressPortPair) pair.getKey();

          // Prints how many seconds has passed since the feed is last verified.
          System.out.println("Feed from " + inetAddressPortPair.getInetAddress().toString() + ":"
              + inetAddressPortPair.getPort() + " - count: " + inetAddressPortPair.getCount());

          // If a feed has reached its expiry time, then try to connect to its corresponding ping port on the ContentServer
          // to check if the server is still running. Keep the feed if it is, or discard otherwise
          if (inetAddressPortPair.getCount().equals(Constants.MAX_TIME_COUNT_THRESHOLD)) {
            Socket pingServerSocket = null;
            BufferedReader in = null;
            PrintWriter out = null;
            String pingString = "";
            Integer contentServerClock;
            boolean exceptionCaught = false;
            try {
              pingServerSocket = new Socket(inetAddressPortPair.getInetAddress(),
                  inetAddressPortPair.getPingServerPort());
              in = new BufferedReader(new InputStreamReader(pingServerSocket.getInputStream()));
              out = new PrintWriter(pingServerSocket.getOutputStream(), true);
              
              // Increment the lamport clock whenever a feed reaches its expiry time as it's considered a major event.
              synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
                out.println(lamportClock.toString());
                lamportClock.increment();
              }
              contentServerClock = Integer.parseInt(in.readLine());
              
              synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
                lamportClock.setValue(Integer.max(lamportClock.toInteger(), contentServerClock) + 1);
                System.out.println("Lamport clock: " + lamportClock.toString());
              }
              pingString = in.readLine();
              in.close();
              // If an exception occurs, any kinds it might be (network exception, integer parse) then assume then the content server is down.
            } catch (Exception exception) {
              System.out.println("Feed from " + inetAddressPortPair.getInetAddress().toString() + ":"
                  + inetAddressPortPair.getPort().toString() + " removed! - ContentServer not reachable");
              synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
                lamportClock.increment();
              }
              iterator.remove();
              exceptionCaught = true;
            }
            if (!exceptionCaught) {
              // If no exception is caught, and the ping reply given by the content sever matches the expected value then
              // renew the feed.
              if (pingString != null && pingString.equals(Constants.PING_SEQUENCE)) {
                inetAddressPortPair.resetCount();
                System.out.println("Feed from " + inetAddressPortPair.getInetAddress().toString() + ":"
                    + inetAddressPortPair.getPort().toString() + " renewed!");
              } else {
                // If the ping response doesn't match then remove the feed
                System.out.println("Feed from " + inetAddressPortPair.getInetAddress().toString() + ":"
                    + inetAddressPortPair.getPort().toString()
                    + " removed! - Invalid ping response from ContentServer!");
                iterator.remove();
              }
            }
            // Show how many feeds remaining in the server.
            System.out.println("Remaining feeds: " + feedDataMap.size());
          } else {
            inetAddressPortPair.incrementCount();
          }
        }
      }
    }
  }
}
