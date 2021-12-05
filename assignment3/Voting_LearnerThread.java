import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// This is the learner class
// This class will set up its own ServerSocket in order to hear from 
// the acceptors when they send commit messages.
// This class waits until a majority of acceptors has agreed on a value
// in which case that value has been voted to be commited by at least n / 2 + 1
// of the nodes.
// When the learner knows that the majority has decided to go with this value
// Then it will commit(In this case prints to the terminal, and dump to a log file)
// Each learner will create a log file M<client number>.log
// It is expected that all log files will contain the same value from one to another
// This represents the data that is commited by the learner when the majority 
// has agreed.
// A simple test case will be to test each contents of MXX.log by diffing it 
// with the first one. They all should match.

public class Voting_LearnerThread extends Thread{
  String memberType;
  Integer myClientNumber;
  Integer numberOfClients;
  StringBuffer previouslyAcceptedMessage;
  ServerSocket learnerSocket;
  HashMap<Integer, Integer> clientPortMap;
  
  Voting_LearnerThread(String memberType, Integer myClientNumber, 
                        Integer numberOfClients, 
                        StringBuffer previouslyAcceptedMessage,
                        ServerSocket learnerSocket,
                        HashMap<Integer, Integer> clientPortMap) {
    this.memberType = memberType;
    this.myClientNumber = myClientNumber;
    this.numberOfClients = numberOfClients;
    this.previouslyAcceptedMessage = previouslyAcceptedMessage;
    this.learnerSocket = learnerSocket;
    this.clientPortMap = clientPortMap;
  }
  
  @Override
  public void run() {
    
    PrintWriter outputFileWriter = null;
    
    try {
      outputFileWriter = new PrintWriter("M" + 
                                         myClientNumber.toString() + ".log");
    } catch (Exception exception) {
      System.out.println("Failed to create log file");
    }
    
    while (true) {
      HashMap<String, Integer> stringCountMap = new HashMap<>();
      try {
        while (true) {
          Socket inSocket = learnerSocket.accept();
          BufferedReader bufferedReader = 
            new BufferedReader(new InputStreamReader(inSocket.getInputStream()));

          String messageType = bufferedReader.readLine();
          
          // Not actually needed anymore. But might be useful later for assignment 4.
          if (messageType.equals("RESET")) {
            bufferedReader.close();
            inSocket.close();
            previouslyAcceptedMessage.delete(0, 
                                             previouslyAcceptedMessage.length());
            break;
          }
          
          Integer acceptorClientNumber = 
            Integer.parseInt(bufferedReader.readLine());
          String proposedMessage = bufferedReader.readLine();
          
          if (Constants.DEBUG)
            System.out.println("LEARNER: " + 
                               messageType + " - " + acceptorClientNumber + 
                               " - " + proposedMessage);
          
          if (!stringCountMap.containsKey(proposedMessage)) {
            stringCountMap.put(proposedMessage, 0);
          }
          Integer proposedMessageCount = stringCountMap.get(proposedMessage) + 1; 
          stringCountMap.put(proposedMessage, 
                              proposedMessageCount);
          
          if (proposedMessageCount > (numberOfClients / 2)) {
            // if (Constants.DEBUG)
            String statusString = "Person elected: " + proposedMessage;
            
            // Erase the previously accepted message once a message is commited 
            // by the learner. This acts like a reset switch, as all of the
            // nodes will have to agree on commiting. This makes sure that the
            // acceptor no longer remembers the old value, or otherwise one
            // value will be stuck forever as the one commited.
            previouslyAcceptedMessage.delete(0, 
                                             previouslyAcceptedMessage.length());
                        
            System.out.println(statusString);
            outputFileWriter.println(statusString);
            outputFileWriter.flush();
            break;
          }
          
          bufferedReader.close();
          inSocket.close();
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
