import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// This is the Client class.
// This component will try to connect and retrieve feeds from the aggregate sever,
// and parse the ATOM feed and present it an easily understandable way.
public class Client {
  private static Integer lamportClock = 0;

  // This function checks if the ATOM xml contains the element with the requested tagname.
  // and prints it if it does, or otherwise prints nothing.
  private static boolean parseAndDipslayElement(Element element, String tagName, String prefix, Integer depth) {
    NodeList elementNodes = element.getElementsByTagName(tagName);
    for (Integer i = 0; i < depth; i++)
      System.out.print("\t");
    if (elementNodes.getLength() > 0)
      System.out.println(prefix + ": " + elementNodes.item(0).getTextContent());
    return true;
  }

  // This function parses a NodeList. It calls parseAndDisplay element for any of the supported elements,
  // and leve the task of figuring out whether the elemnts exists to the function.
  // For an entry element, as they pretty much have the same sturcture as the root node,
  // it will recursively calls itself and parse the content of the entry.
  private static boolean parseAndDipslayNodeList(NodeList feedList, Integer depth) {
    for (Integer index = 0; index < feedList.getLength(); index++) {
      Node feedNode = feedList.item(index);
      if (feedNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) feedNode;
        parseAndDipslayElement(element, "title", "Title", depth);
        parseAndDipslayElement(element, "subtitle", "Subtitle", depth);
        parseAndDipslayElement(element, "link", "Link", depth);

        NodeList authorNodeList = element.getElementsByTagName("author");
        if (authorNodeList.getLength() > 0) {
          Element authorNameElement = (Element) authorNodeList.item(0);
          parseAndDipslayElement(authorNameElement, "name", "Author name", depth);
        }

        parseAndDipslayElement(element, "updated", "Date updated", depth);
        parseAndDipslayElement(element, "id", "ID", depth);
        parseAndDipslayElement(element, "summary", "Summary", depth);

        NodeList entryList = element.getElementsByTagName("entry");
        parseAndDipslayNodeList(entryList, depth + 1);
      }
      // If it's the root node in the feed, then print a newline.
      if (depth == 1)
        System.out.println("");
    }
    return true;
  }

  // Nothing major here, the main function pretty much tries to get the feeds from the AggregateServer
  // by sendin a get requesdt, which also contains a lamport clock (Will always be 0 in this case)
  // then calls the helper functions to parse the feed, and print the lamport clock.
  public static void main(String[] args) {
    String hostName = "localhost";
    Integer portNumber = Constants.AGGREGATE_SERVER_PORT_NUMBER;

    if (args.length > 0) {
      hostName = args[0];
    }
    if (args.length > 1) {
      portNumber = Integer.parseInt(args[1]);
    }
    
    Socket aggregateServerSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    try {
      aggregateServerSocket = new Socket(hostName, portNumber);
      out = new PrintWriter(aggregateServerSocket.getOutputStream(),
        true);
      in = new BufferedReader(
        new InputStreamReader(aggregateServerSocket.getInputStream()));
    }
    catch (Exception e) {
      System.out.println("Failed to connect to aggregate server, make sure it's started on port: " + portNumber.toString());
      return;
    }
    
    try {
      out.println("GET");
      out.println("Clock: " + lamportClock.toString());
      out.println(Constants.TERMINATE_SEQUENCE);
      String resultFromServer = "";
      String wholeInput = "";
      try {
            while ((resultFromServer = in.readLine()) != null &&
                  !resultFromServer.equals(Constants.TERMINATE_SEQUENCE)) {
                      wholeInput += resultFromServer += "\r\n";
          }
      } catch(Exception exception) {
        exception.printStackTrace();
        System.out.println("Error while reading from Aggregate Server!");
        return;
      }
      // System.out.println(wholeInput);
      InputStream inputStream = new ByteArrayInputStream(wholeInput.getBytes(StandardCharsets.UTF_8));
      
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = null; 
      Document document = null; 

      documentBuilder = documentBuilderFactory.newDocumentBuilder();
      document = documentBuilder.parse(inputStream);
      document.getDocumentElement().normalize(); 
      
      try {
        Integer lamportClockFromXMLAttribute = Integer.parseInt(document.getDocumentElement().getAttributes().getNamedItem("clock").getTextContent());
        lamportClock = Integer.max(lamportClock, lamportClockFromXMLAttribute) + 1; 
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println("Lamport clock: " + lamportClock.toString());
      // System.out.println("Root element :" + document.getDocumentElement().getNodeName());
      
      NodeList feedList = document.getElementsByTagName("feed");
      parseAndDipslayNodeList(feedList, 0);
    } catch (Exception e) {
      System.out.println("Invalid XML detected from Aggregate Server!");
      System.out.println("Terminating...");
      return;
    }

  }
}


