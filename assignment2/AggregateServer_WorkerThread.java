import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import org.apache.commons.lang3.mutable.MutableInt;

// This thread is responsible for handling requests from both the
// clients and the content servers. For clients, this thread will return a message containing the feeds in ATOM
// format. Whereas for handling the requests from the ContentServers, this thread will add an entry
// to the feed hashmap which maps the InetAdressPortPair unique to each connection to each of their feed.
public class AggregateServer_WorkerThread extends Thread{
  
  private HashMap<InetAddressPortPair, String> feedDataMap;
  private MutableInt lamportClock;
  private Socket socket;
  
  public AggregateServer_WorkerThread(HashMap<InetAddressPortPair, String> feedDataMap,
                                       Socket socket,
                                       MutableInt lamportClock) {
    this.feedDataMap = feedDataMap;
    this.socket = socket;
    this.lamportClock = lamportClock;
  }
    
  public void run() {
    try {
      
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // While loop is no longer important as the pingThread takes care of updates now., but it's kept in case of ContentServer wanting to update it's feed.
      // while (true) {
        String httpRaw = "";
        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null && !inputLine.equals(Constants.TERMINATE_SEQUENCE)) {
          httpRaw += inputLine + "\r\n";
        }
        if (inputLine == null)
          return;
        // Check if this does what I think it does.
        HttpBundle httpBundle = Utils.parseHttpRequest(httpRaw);

        if (httpBundle.isInvalid()) {
          System.out.println("Invalid packet detected!");
          return;
        }

        HttpHeader httpHeader = httpBundle.getHttpHeader();

        // System.out.println("Content = " + httpBundle.getContent());
        System.out.println("PingServer = " + httpHeader.pingServerPort);

        if (httpHeader.requestType.equals("PUT")) {
          InetAddressPortPair inetAddressPortPair = new InetAddressPortPair(socket.getInetAddress(), socket.getPort(),
              httpHeader.pingServerPort, httpBundle.getContentLines());

          synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CODE) {
            if (feedDataMap.containsKey(inetAddressPortPair)) {
              feedDataMap.remove(inetAddressPortPair);
            }
            feedDataMap.put(inetAddressPortPair, httpBundle.getContent());
          }
          synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
            lamportClock.setValue(Integer.max(lamportClock.toInteger(), httpHeader.clock) + 1);
          }
        } else if (httpHeader.requestType.equals("GET")) {
          String feedForClient = "";
          feedForClient += "<?xml version='1.0' encoding='iso-8859-1' ?>\r\n";
          synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CLOCK) {
            lamportClock.setValue(Integer.max(lamportClock.toInteger(), httpHeader.clock) + 1);
            feedForClient += "<xml clock='" + lamportClock.toString() + "'>\r\n";
          }
          synchronized (Constants.AGGREGATE_SERVER_SYNCHRONIZED_CODE) {
            for (Object mapValue : feedDataMap.values()) {
              String storedFeed = (String) mapValue;
              feedForClient += storedFeed;
            }
          }
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          feedForClient += "</xml>\r\n";
          out.printf("%s", feedForClient);
          out.println(Constants.TERMINATE_SEQUENCE);
 
          // return;
        }
        System.out.println("Lamport Clock: " + lamportClock.toString());
      // }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
