import java.net.InetAddress;


// This class is mainly used for a key value in the feed hashmap.
// Information contained in this class is used to uniquesly identify a connection
// from the AggregateServer to the ContentServer.
// tHE hashCode and equals functions are overriden to help the hashmap unqiuely identify an instance of this class.
public class InetAddressPortPair {
  private InetAddress inetAddress;
  private Integer port;
  private Integer count;
  private Integer pingServerPort;
  private Integer contentLines;
  
  @Override
  public int hashCode() {
    return inetAddress.hashCode() + port.hashCode();
  }
  
  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (getClass() != object.getClass()) return false;
    InetAddressPortPair inetAddressPortPair = (InetAddressPortPair)object;
    return inetAddress.equals(inetAddressPortPair.inetAddress) &&
        port.equals(inetAddressPortPair.port);
  }
  
  public InetAddressPortPair(InetAddress inetAddress, 
                             Integer port, Integer pingServerPort, Integer contentLines) {
    count = Constants.MAX_TIME_COUNT_THRESHOLD;
    this.inetAddress = inetAddress;
    this.port = port;
    this.pingServerPort = pingServerPort;
    this.contentLines = contentLines;
  }
  
  public InetAddress getInetAddress() {
    return inetAddress;
  }
  
  public Integer getPort() {
    return port;
  }
  
  public void incrementCount() {
    count++;
  }  
  
  public Integer getCount() {
    return count;
  }
  
  public Integer getPingServerPort() {
    return pingServerPort;
  }
  
  public Integer getContentLines() {
    return contentLines;
  }
  
  public void resetCount() {
    count = 0;
  }
}