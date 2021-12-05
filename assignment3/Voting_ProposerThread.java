import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


// This is the proposer class.
// The proposer class sets up a ServerSocket with the port defined in Constants.java
// The proposer component has two modes of actions:
// 1. Send a "propose" message to the acceptor, along with the value and proposal number
// as well as other extra information such as the proposer's client id.
// It's the proposer's best 
// 2. Depending
public class Voting_ProposerThread extends Thread{
  String memberType;
  Integer myClientNumber;
  Integer numberOfClients;
  ServerSocket proposerSocket;
  HashMap<Integer, Integer> clientPortMap;
  
  Integer counter = 0;
  
  Voting_ProposerThread(String memberType, Integer myClientNumber, 
                        Integer numberOfClients, ServerSocket proposerSocket,
                        HashMap<Integer, Integer> clientPortMap) {
    this.memberType = memberType;
    this.myClientNumber = myClientNumber;
    this.numberOfClients = numberOfClients;
    this.proposerSocket = proposerSocket;
    this.clientPortMap = clientPortMap;
  }
  
  public void run() {
    // Create a random number generator, if the number is greater than certain
    // threshold then this proposer will propose a new thread.
    Random randy = new Random();
    
    while (true) {
      HashMap<String, Integer> stringCountMap = new HashMap<>();
      ArrayList<Integer> preparedClients = new ArrayList<>();
      String myProposedString = "M" + myClientNumber.toString();
      
      Integer n = 0;
      try {
        // If the random number is more than the double value got from
        // Double.GetProposerProbability() then send a propose message to all
        // nodes.
        if (randy.nextDouble() <= Constants.GetProposerProbability(memberType)) {
          for (Integer i = 0; i < numberOfClients; i++) {
            // Create a new socket and send a message to all the acceptors of
            // the other nodes
            try {
                Socket outSocket = new Socket("localhost",
                  clientPortMap.get(i) +
                   Constants.ACCEPTOR_SOCKET_PORT_OFSET);
                PrintWriter printWriter = 
                  new PrintWriter(outSocket.getOutputStream(), true);
              // The current proposal number is the counter which is the 
              // number of proposals that has been made by this curerent node
              // * Constant.MAX_NUMBER_OF_CLIENTS + the client number,
              n = myClientNumber + counter * Constants.MAX_NUMBER_OF_CLIENTS;
              // The message format is as follows:
              // ========================
              // PREPARE
              // Client number
              // Proposal number
              // Proposed value
              // END_FLAG <-- to signify end of transmission
              // ========================
              printWriter.println("PREPARE");
              printWriter.println(myClientNumber.toString());
              printWriter.println(n.toString());
              printWriter.println(myProposedString);
              printWriter.println(Constants.END_TRANSMISSION_SEQUENCE);
              printWriter.close();
              outSocket.close();
            } catch (Exception exception) {
              // If a exception occuts, assume that this is caused by a node
              // breaking down. In this case, just skip and don't retry to send
              // a proposal. The worse case is that the node is not actually
              // down and a proposal might be delayed.
              continue;
            }
          }
          // Increase counter from 0 to 10. This is done tis way in order to 
          // make proposal numbers more unpredicatble, and so we have more
          // variations in the messages that are proposed.
          counter += (int)(randy.nextDouble() * 10);
          
          // Set timeout for the proposer socket
          // If Constants.PROPOSER_TURNAROUND_TIME has elapse from the time
          // this proposer has started listening for a promise message
          // then stop listening, and go do the next step.
          proposerSocket.setSoTimeout(2 * Constants.PROPOSER_TURNAROUND_TIME);
          
          Integer currentMaxTagNumber = 0;
          
          // While the proposerSokcet has not timed out yet, continue to try
          // accepting a connection. This connection expects a promise message
          // from an acceptor
          while (true) {
            try {
              Socket inSocket = proposerSocket.accept();
              BufferedReader bufferedReader = 
              new BufferedReader(
                new InputStreamReader(inSocket.getInputStream()));
              
              // The promise message follows this format
              // =============================
              // ACCEPTED
              // Client Id <-- the acceptor's client number
              // Proposal number <-- the proposer's previous message proposal number
              // Promised value <-- either empty or somethin else the node has previously promised
              // =============================
              String messageType = bufferedReader.readLine();
              Integer clientId = Integer.parseInt(bufferedReader.readLine());
              Integer tagNumber = Integer.parseInt(bufferedReader.readLine());
              
              currentMaxTagNumber = Integer.max(tagNumber, currentMaxTagNumber);
              
              String proposedValue = bufferedReader.readLine();
              
              // If the previously proposed value is empty, then propose the 
              // proposer's own value.
              if (proposedValue.equals(""))
                proposedValue = myProposedString;
              
              if (Constants.DEBUG)
                System.out.println("PROPOSER : " + messageType + " - " + 
                                   tagNumber.toString() + " - " + 
                                   proposedValue + " --- " + n.toString());
            
              // Add the clientId to the list of clients that has "prepared"
              // or promised.
              preparedClients.add(clientId);
              
              // And count the number of time a value has been promised by all 
              // the nodes that send a response to this proposer
              if (!stringCountMap.containsKey(proposedValue))
                stringCountMap.put(proposedValue, 0);
              Integer proposedValueCount = 
                stringCountMap.get(proposedValue) + 1;
              stringCountMap.put(proposedValue, proposedValueCount);

              bufferedReader.close();
              inSocket.close();
            } catch (Exception exception) {
              break;
            }
          }
          
          Integer maxVote = 0;
          String maxVotedString = "";
          Iterator iterator = stringCountMap.entrySet().iterator();
          
          // Go through all the entries in the HashMap and count the maximum
          // proposed values out of all.
          while (iterator.hasNext()) {
            Map.Entry<String, Integer> pair = (
              Map.Entry<String, Integer>) iterator.next();
            if (Constants.DEBUG)
              System.out.println("PAIR :: " + 
                                 pair.getKey() + " - " + pair.getValue());
            if (((Integer) pair.getValue()) > maxVote) {
              maxVote = (Integer) pair.getValue();
              maxVotedString = (String) pair.getKey();
            }
          }
          
          if (maxVotedString.equals("")) {
            maxVotedString = myProposedString;
          }
          
          if (Constants.DEBUG)
            System.out.println("PROPOSER: MAX PROPOSED : " + 
                               maxVotedString + " - " + maxVote.toString() + 
                               " votes");

          // If the maximum number of the string proposed in the HashMap is 
          // greater than the number of paxos nodes in the network / 2, then
          // The proposal goes on. In this case, then send an accept message
          // to the acceptor.
          if (maxVote > numberOfClients / 2) {
            for (Integer clientId : preparedClients) {
              try {
                Socket outSocket = new Socket("localhost",
                    clientPortMap.get(clientId) + Constants.ACCEPTOR_SOCKET_PORT_OFSET);
                PrintWriter printWriter = 
                  new PrintWriter(outSocket.getOutputStream(), true);
                  
                // The accept message is in the folowing format
                // ======================================
                // ACCEPT
                // Client number
                // Proposal number
                // The final proposed value
                // COMMUNICATION_END_FLAG
                // ======================================
                printWriter.println("ACCEPT");
                printWriter.println(myClientNumber.toString());
                printWriter.println(currentMaxTagNumber.toString());
                printWriter.println(maxVotedString);
                printWriter.println(Constants.END_TRANSMISSION_SEQUENCE);
                outSocket.close();
              } catch (Exception exception) {
                continue;
              }
            }
          }
        }
        Thread.sleep(Constants.PROPOSER_TURNAROUND_TIME);
        
        
      // Send RESET to all nodes ( not really needed for now )  
      //   if (myClientNumber == 0) {
      //   for (Integer i = 0; i < numberOfClients; i++) {
      //     try {
      //       Socket acceptorSocket = 
      //       new Socket("localhost", Constants.STARTING_PORT_NUMBER +
      //             i * 4 + Constants.ACCEPTOR_SOCKET_PORT_OFSET);
      //       Socket learnerSocket = new Socket("localhost",
      //         Constants.STARTING_PORT_NUMBER + i * 4 + Constants.LEARNER_SOCKET_PORT_OFFSET);
      //     PrintWriter acceptorPrintWriter = new PrintWriter(acceptorSocket.getOutputStream(), true);
      //     acceptorPrintWriter.println("RESET");
      //     PrintWriter learnerPrintWriter = new PrintWriter(learnerSocket.getOutputStream(), true);
      //     learnerPrintWriter.println("RESET");
      //     acceptorSocket.close();
      //     learnerSocket.close();
      //     } catch (Exception exception) {
      //       continue;
      //     }
          
      //   }
      // }
          
      } catch (Exception exception) {
        if (Constants.DEBUG)
          System.out.println("Failed to propose: disconnected node detected");
      }

    }
    
  }
}
