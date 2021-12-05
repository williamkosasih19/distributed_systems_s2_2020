public class Constants {
  public static Boolean DEBUG = false;
  
  public static final Integer STARTING_PORT_NUMBER = 50000;
  public static final Integer PROPOSER_SOCKET_PORT_OFFSET = 1;
  public static final Integer ACCEPTOR_SOCKET_PORT_OFSET = 2;
  public static final Integer LEARNER_SOCKET_PORT_OFFSET = 3;
  public static final Integer WAIT_TIME_BEFORE_START = 10;
  public static final Integer PROPOSER_TURNAROUND_TIME = 125;
  public static final Integer MAX_NUMBER_OF_CLIENTS = 100;
  
  public static final String START_VOTING = "START";
  public static final String END_TRANSMISSION_SEQUENCE = "ENDEND";
  
  // Member M1 is the most likely member to propose
  public static double GetProposerProbability(String memberType) {
    if (memberType.equals("M1"))
      return 0.5;
    if (memberType.equals("M2"))
      return 0.5;
    if (memberType.equals("M3"))
      return 0.5;
    return 0.5;
  }
  
  public static Integer GetAcceptorResponseTime(String memberType) {
    // If councilorr type is M1, then respond immediately
    if (memberType.equals("M1"))
      return 0;
    // If councilor type is M2, then on very small occasion, respond immediately,
    // Otherwise wait 250 ms to respond
    if (memberType.equals("M2")) {
      if (Math.random() < 0.1)
        return 0;
      return 250;
    }
    // If councilor type is M4, then the response time varies between 0 to 500 ms
    if (memberType.equals("M3"))
      return 125;
    return (int)(Math.random() * 500.0);
  }
}
