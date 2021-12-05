import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.mutable.MutableInt;

// This is the SaveThread component.
// This component, will export the value of each feeds in the map every s seconds, and put them into a text file
// along with the essential informations such as the address of the ContentServer, the heartbeat port, and the message length.
public class AggregateServer_SaveThread extends Thread {
  private HashMap<InetAddressPortPair, String> feedDataMap;
  private MutableInt lamportClock;
  public AggregateServer_SaveThread(HashMap<InetAddressPortPair, String> feedDataMap,
                                    MutableInt lamportClock) {
    this.feedDataMap = feedDataMap;
    this.lamportClock = lamportClock;
  }
  public void run() {
    while (true) {
      HashMap<InetAddressPortPair, String> feedDataMapSnapshot = null;

      // Clone the feed map to a local copy.
      synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CODE) {
        feedDataMapSnapshot = (HashMap<InetAddressPortPair, String>) feedDataMap.clone();
      }
      Iterator iterator = feedDataMapSnapshot.entrySet().iterator();
      PrintWriter printWriter = null;
      try {
        printWriter = new PrintWriter("backup.log");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      // Go through every entry of feed in the map, and append nescessary informations sucha as:
      // 1. The address of the ContentServer
      // 2. The main port of the ContentServer (not really needed, but is just there to make it backwards compatible with my previous implementation)
      // 3. The ContentServer's ping port address (heartbeat)
      // 4. The number of lines in the feed
      while (iterator.hasNext()) {
        HashMap.Entry pair = (HashMap.Entry) iterator.next();
        InetAddressPortPair inetAddressPortPair = (InetAddressPortPair) pair.getKey();
        String content = (String) pair.getValue();
        printWriter.println(inetAddressPortPair.getInetAddress().getHostAddress());
        printWriter.println(inetAddressPortPair.getPort().toString());
        printWriter.println(inetAddressPortPair.getPingServerPort().toString());
        printWriter.println(inetAddressPortPair.getContentLines());
        printWriter.printf("%s", content);
      }
      printWriter.println(Constants.TERMINATE_SEQUENCE);
      printWriter.flush();
      printWriter.close();
      synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
        lamportClock.increment();
      }
      try {
        Thread.sleep(5000);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
