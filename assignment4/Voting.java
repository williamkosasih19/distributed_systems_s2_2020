import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

// Taken from Paxos Made Simple by L. Lamport

// Phase 1. (a) A proposer selects a proposal number n and sends a prepare
// request with number n to a majority of acceptors.
// b. If an acceptor receivers a prepare request with number n greater than that
// of any prepare request to which it has already responded, then it responds to
// the request with a promise to accept anh more proposals numbered less than n 
// and with the highest-numbered proposal (if any) that it has accepted.

// Phase 2. (a) If the proposer receives a response to its prepare requests (numbered) n
// from a majority of acceptors, then it sends an accept request to each of those acceptors
// for a proposal numbered n with a value b, where v is the value of the highest-
// numbered proposal among the responses, or is any value if the responses reported no proposals.
// (b) If an acceptor receives an accept request for a proposal numbered n, 
// it accepts the proposal unless it has already responded to a prepare request
// having a number greater than n.

// Phase 3. To learn that a value has been chosen, a learner must dind out that a 
// proposal has been accpeted by a majority of acceptors. The obvious algorithm is to have
// each acceptor, whenever it accepts a proposal respond to all learnerrs, 
// sending them the proposal

class Voting {
  public static void main(String[] args) {
    ServerSocket generalSocket = null;
    ServerSocket proposerSocket = null;
    ServerSocket acceptorSocket = null;
    ServerSocket learnerSocket = null;
    
    Integer numberOfClients = 1;
    Integer myClientNumber = 0;
    
    String memberType = args[0];
    StringBuffer previouslyAcceptedMessage = new StringBuffer();
    
    // Set debug flag here
    if (args.length > 1 && args[1].equals("DEBUG"))
      Constants.DEBUG = true;
    
    for (Integer i = Constants.STARTING_PORT_NUMBER; i < 65536; i += 4) {
      try {
        generalSocket = new ServerSocket(i);
        proposerSocket = 
          new ServerSocket(i + Constants.PROPOSER_SOCKET_PORT_OFFSET);
        acceptorSocket = 
          new ServerSocket(i + Constants.ACCEPTOR_SOCKET_PORT_OFSET); 
        learnerSocket =
          new ServerSocket(i + Constants.LEARNER_SOCKET_PORT_OFFSET);
      break;
      } catch (Exception exception) {}
    }
    
    System.out.println("GeneralSocket port: " + 
                       Integer.toString(generalSocket.getLocalPort()));
    System.out.println("ProposerSocket port: " + 
                       Integer.toString(proposerSocket.getLocalPort()));
    System.out.println("acceptorSocket port: " + 
                       Integer.toString(acceptorSocket.getLocalPort()));
    System.out.println("learnerSocket port: " + 
                       Integer.toString(learnerSocket.getLocalPort()));
    
    HashMap<Integer, Integer> clientPortMap =  new HashMap<>();
    
    if (generalSocket.getLocalPort() == Constants.STARTING_PORT_NUMBER) {
      clientPortMap.put(0, Constants.STARTING_PORT_NUMBER);
      while (true) {
        boolean startVoting = false;
        try {
          Socket connectionSocket = generalSocket.accept();
          Integer currentNumberOfClients = numberOfClients++;
          
          BufferedReader in = new BufferedReader(
            new InputStreamReader(connectionSocket.getInputStream()));
          Integer clientPortNumber = Integer.parseInt(in.readLine());
          clientPortMap.put(currentNumberOfClients, clientPortNumber);
          
          PrintWriter out = 
            new PrintWriter(connectionSocket.getOutputStream(), true);
          out.println(currentNumberOfClients.toString());
          String currentLine = "";
          
          while (((currentLine = in.readLine()) != null && 
                  !currentLine.equals(Constants.END_TRANSMISSION_SEQUENCE))) {
                    if (currentLine.equals(Constants.START_VOTING))
                      startVoting = true;
                  }
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
        if (startVoting)
          break;
      }
      for (Integer i = 0; i < numberOfClients; i++) {
        Socket printWriterSocket = null;
        try {
          printWriterSocket = 
            new Socket("localhost",
                       clientPortMap.get(i));
          PrintWriter printWriter = 
            new PrintWriter(printWriterSocket.getOutputStream(), true);
            
          printWriter.println(numberOfClients.toString());
          for (int j = 0; j < numberOfClients; j++)
            printWriter.println(clientPortMap.get(j).toString());
            
          printWriter.println(Constants.END_TRANSMISSION_SEQUENCE);
          printWriter.close();
          printWriterSocket.close();
        } catch (Exception exception) {
          exception.printStackTrace();
          return;
        }
      }
    }
    else {
      Socket firstServerSocket = null;
      PrintWriter firstServerPrintWriter = null;
      try {
        firstServerSocket = 
          new Socket("localhost", Constants.STARTING_PORT_NUMBER);
        firstServerPrintWriter = 
            new PrintWriter(firstServerSocket.getOutputStream(), true);
        firstServerPrintWriter.println(Integer.toString(generalSocket.getLocalPort()));
      } catch (Exception exception) {
        System.err.println("First node did not start at port " + Constants.STARTING_PORT_NUMBER.toString() + 
                           " please wait a moment until the port is available again...");
        return;
      }
      if (args.length > 1 && args[1].equals("start")) {
        firstServerPrintWriter.println(Constants.START_VOTING);
      }
      firstServerPrintWriter.println(Constants.END_TRANSMISSION_SEQUENCE);
      try {
        BufferedReader firstServerBufferedReader = 
          new BufferedReader(
            new InputStreamReader(firstServerSocket.getInputStream()));
          
        String clietNumberString = firstServerBufferedReader.readLine();
        myClientNumber = Integer.parseInt(clietNumberString);
        System.out.println("My Client Number String: " + clietNumberString);
        firstServerPrintWriter.close();
        
        firstServerSocket.close();
        
        Socket registerSocket = generalSocket.accept();
        BufferedReader bufferedReader = 
            new BufferedReader(
              new InputStreamReader(registerSocket.getInputStream()));
        numberOfClients = Integer.parseInt(bufferedReader.readLine());
        for (int i = 0; i < numberOfClients; i++) {
          clientPortMap.put(i, Integer.parseInt(bufferedReader.readLine()));
        }
      } catch (Exception exception) {
        exception.printStackTrace();
        return;
      }
    }
    System.out.println("Number of Clients: " + numberOfClients.toString());
    System.out.println("My Client Number " + myClientNumber.toString());
    System.out.println("Client map: ");
    for (int i = 0; i < numberOfClients; i++) {
      System.out.println("Client #" + Integer.toString(i) + " : port " + 
                                                       clientPortMap.get(i).toString());
    }
    
    Voting_ProposerThread proposer = 
    new Voting_ProposerThread(memberType, 
                              myClientNumber, 
                              numberOfClients,
                              proposerSocket,
                              clientPortMap);
    
    Voting_AcceptorThread acceptor = 
      new Voting_AcceptorThread(memberType, 
                                myClientNumber, 
                                numberOfClients, 
                                previouslyAcceptedMessage, acceptorSocket,
                                clientPortMap);
    Voting_LearnerThread learner = 
    new Voting_LearnerThread(memberType, myClientNumber, numberOfClients,
                             previouslyAcceptedMessage, learnerSocket, clientPortMap);
    
    proposer.start();
    acceptor.start();
    learner.start();
  }
};