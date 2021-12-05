import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// This is the acceptor thread of the voting client.
// The port offset of this thread is defined in 
// Constants.ACCEPTOR_SOCKET_PORT_OFSET.

// This thread has two mode of actions:
// 1. Receive prepare message from a proposer
// and depending on the situation, will either
//    1a. Send a promise message with an empty string ""
// This is the case when the prepare message received from the proposer has the
// highest number so far, and there are no previously accepted prepare message
//    2a. Send a promise message with a previously prepared value
// This is the case when the prepare message received from the proposer has th
// highest number so far, but there has been another message that was previously
// accepted by the acceptor as it was then the highest number
//    3a. Ignore the message
// This is the case when the prepare message received has a lower number than
// another message that waas previously seen by the acceptor
//
// 2. Receive a commit message from a proposer
// and depending on the situation, will either
//    2a. Send an "accepted" message to all the learners
//    2b. Ignore, in case the acceptor has accepted a bigger proposal number
// by another proposer while waiting for a commit message.
public class Voting_AcceptorThread extends Thread{
  String memberType;
  Integer myClientNumber;
  Integer numberOfClients;
  StringBuffer previouslyAcceptedMessage;
  ServerSocket acceptorSocketSocket;
  HashMap<Integer, Integer> clientPortMap;
  
  Integer acceptedCounter = 0;
  
  Voting_AcceptorThread(String memberType, Integer myClientNumber, 
                        Integer numberOfClients, 
                        StringBuffer previouslyAcceptedMessage,
                        ServerSocket acceptorSocket,
                        HashMap<Integer, Integer> clientPortMap) {
    this.memberType = memberType;
    this.myClientNumber = myClientNumber;
    this.numberOfClients = numberOfClients;
    this.previouslyAcceptedMessage = previouslyAcceptedMessage;
    this.acceptorSocketSocket = acceptorSocket;
    this.clientPortMap = clientPortMap;
  }

  public void run() {
      while (true) {
        try {
          Socket socket = acceptorSocketSocket.accept();
          BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
          
          String command = bufferedReader.readLine();
          // Reset the previously acceoted message string, in case a message 
          // from proposer of client 1 sends a RESET command.
          // This is not necessary for the program to run correctly, but I
          // added this just to make sure that the program starts "fresh"
          // after some time.
          // This is actually no longer needed now, but I decided to keep it
          // just in case.
          if (command.equals("RESET")) {
            // acceptedCounter = 0;
            previouslyAcceptedMessage.delete(0, previouslyAcceptedMessage.length());
            bufferedReader.close();
            socket.close();
            continue;
          }
          Integer proposerClientId = Integer.parseInt(bufferedReader.readLine());
          Integer proposalNumber = Integer.parseInt(bufferedReader.readLine());
          String proposedMessage = bufferedReader.readLine();
          
          if (Constants.DEBUG)
            System.out.println("ACCEPTOR: " + command + " - " + 
                               proposalNumber.toString() + " - " + proposedMessage);
          
          if (command.equals("PREPARE")) {
            if (proposalNumber > acceptedCounter) {
              try {
                acceptedCounter = proposalNumber;

              Socket outSocket = new Socket("localhost",
                clientPortMap.get(proposerClientId) + 
                      Constants.PROPOSER_SOCKET_PORT_OFFSET);
              PrintWriter printWriter = 
                new PrintWriter(outSocket.getOutputStream(), true);
                
              // Sleep for x ms according to the member type. This is one of the 
              // requirements in the assignment spec.
              // For example if member type is M2, then on most occasions respond
              // after 500 ms, but on small occasion (like when he's at the 
              // internet cafe) then respond instantly
              Integer sleepTime = Constants.GetAcceptorResponseTime(memberType); 
              if (Constants.DEBUG)
                System.out.println("Waiting for: " + sleepTime +"ms before responding...");
              Thread.sleep(sleepTime);
                
              printWriter.println("PREPARED");
              printWriter.println(myClientNumber.toString());
              printWriter.println(proposalNumber.toString());
              printWriter.println(previouslyAcceptedMessage);
              
              if (previouslyAcceptedMessage.toString().equals("")) {
                previouslyAcceptedMessage.delete(0, previouslyAcceptedMessage.length());
                previouslyAcceptedMessage.append(proposedMessage);
              }
              printWriter.close();
              outSocket.close();
              } catch (Exception exception) {
                // Skip trying to send a "prepared" message if a network 
                // exception occurs.
                continue;
              }
            }
          } else if (command.equals("ACCEPT") && 
                     proposalNumber >= acceptedCounter) {
                        
            for (Integer i = 0; i < numberOfClients; i++) {
              try {
                Socket outSocket = new Socket("localhost",
                  clientPortMap.get(i) + Constants.LEARNER_SOCKET_PORT_OFFSET);
              PrintWriter printWriter = new PrintWriter(outSocket.getOutputStream(),
                  true);
              
              printWriter.println("ACCEPTED");
              printWriter.println(myClientNumber.toString());
              printWriter.println(proposedMessage.toString());
              
              printWriter.close();
              outSocket.close();
              } catch (Exception exception) {
                // If a network exception occured in this codeblock, then 
                // continue trying to send the "accepted" message to the next
                // learners. It is safe to assume that the associated client has
                // died (for now)
                continue;
              } 
            }
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
  }
}
