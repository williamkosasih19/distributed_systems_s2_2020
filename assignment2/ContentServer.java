import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


// This is the ContentServer component.
// This component pushes the feed to the AggregateServer, by issuing a PUT request
// to the specified address of the AggregateServer.
// This server also sets up a ping socket, for the AggregateServer to connect to
// in order to verify that this (the content server) is still running, and thus
// the AggregateServer will retain the feed.
// The information of this server's (randomly assigned) ping port address 
// is embedded in the http header of the initial PUT request issued
// to the aggregate server.
// This server also has the functionality to convert text feed into the ATOM xml
// format.
public class ContentServer {
  private static Integer availablePortNumber = 0;
  private static ServerSocket pingServerSocket = null;
  private static Integer lamportClock = 0;
  public static void main(String[] args) {
    String hostName = "localhost";
    Integer portNumber = Constants.AGGREGATE_SERVER_PORT_NUMBER;
    String fileName = "feed.txt";
    
    if (args.length > 0) {
      fileName = args[0];
    }
    if (args.length > 1) {
      hostName = args[1];
    }
    if (args.length > 2) {
      portNumber = Integer.parseInt(args[2]);
    }
    
    Socket aggregateServerSocket;
    PrintWriter out = null;
    try {
          aggregateServerSocket = new Socket(hostName, portNumber);
          out = new PrintWriter(aggregateServerSocket.getOutputStream(), true);
    } catch (Exception exception) {
      // exception.printStackTrace();
      System.out.println("Failed to connect to Aggregate Server, make sure it's started on port : " + portNumber.toString());
      return;
    }
    
    File feedFile = new File(fileName);
    Scanner sc = null;
    try {
      sc = new Scanner(feedFile);
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    
    ArrayList<String> feedArrayList = new ArrayList<>();
    feedArrayList.add("<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\">");
    
    Boolean firstPass = true;
    
    // This part of code pretty much just converts key: value pairs to <key>value</key> 
    // which is in XML format.
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      if (line.equals("entry")) {
        if (!firstPass) {
          feedArrayList.add("\t</entry>");
        }
        firstPass = false;
        feedArrayList.add("\t<entry>");
        continue;
      }
       
      String padding = "";
      if (!firstPass)
        padding += "\t";
      Integer colonNumber = line.indexOf(':');
      // If a line doesn't content colon in it then just treat it as an error
      // and ignore the line. If the line happens to be a crucial part of the XML
      // then the client will fail securely, but content server won't treat this as an error.
      if (colonNumber == -1)
        continue;
      String title = line.substring(0, colonNumber);
      String content = line.substring(colonNumber + 1);
      if (title.equals("author")) {
        feedArrayList.add(padding + "\t<author>");
        feedArrayList.add(padding + "\t\t<name>" + content + "</name>");
        feedArrayList.add(padding + "\t</author>");
      } else {
        feedArrayList.add(padding + "\t" + "<" + title + ">" + content + "</" + title + ">");
      }
    }
    feedArrayList.add("\t</entry>");
    feedArrayList.add("</feed>");
    
    String feedToBeSent = "";
    for (String line : feedArrayList) {
      feedToBeSent += line + "\r\n";
    }
    Integer contentLength = feedToBeSent.getBytes().length;
    try {
      pingServerSocket = new ServerSocket(0);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    
    // The ping thread, handles heartbear request from the aggregate server, and replies
    // with certain response defined in Constants.PING_SEQUENCE. The Aggregate server will
    // check this challange-response result in order to determine whether to keep
    // their feed or not.
    Thread pingThread = new Thread(new Runnable() {
      public void run() {
        while (true) {
          try {
          Socket aggregateServerSocket = null;
            availablePortNumber = pingServerSocket.getLocalPort();
            aggregateServerSocket = pingServerSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(aggregateServerSocket.getInputStream()));
            try {
              Integer aggregateServerClock = Integer.parseInt(in.readLine());
              lamportClock = Integer.max(lamportClock, aggregateServerClock) + 1;
              System.out.println("Lamport clock: " + lamportClock.toString());
            } catch (Exception exception) {
              exception.printStackTrace();
            }
            
            PrintWriter out = new PrintWriter(aggregateServerSocket.getOutputStream(), true);
            out.println(lamportClock.toString());
            out.printf("%s\r\n%s", Constants.PING_SEQUENCE, Constants.TERMINATE_SEQUENCE);
            out.close();
            aggregateServerSocket.close();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      }
    });

    // Start the pingThread to handle heartbeat messages. It's started before
    // the serveractually sends the PUT request to the aggreagate server in order
    // to prevent the aggregate server tying to ping the content server before the 
    // ping thread starts
    pingThread.start();
    
    
    out.println("PUT /atom.xml HTTP/1.1");
    out.println("User-Agent: ATOMClient/1/0");
    out.println("Content-Type: application/atom+xml");
    out.println("Content-Length: " + contentLength.toString());
    out.println("PingServer-Port-Number: " + availablePortNumber.toString());
    out.println("Clock: " + lamportClock.toString());


    out.print(feedToBeSent);
    out.println(Constants.TERMINATE_SEQUENCE);
    
    try {
      Thread.sleep(10000);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}