import java.util.HashMap;

// This class is supposed to house various auxilary utilities that may be needed
// by other components.
public class Utils {
  // This function parses raw http request strored in a string, and returns a
  // HttpBundle object which contains:
  // 1. HttpHeader: A class that houses the header information in each of the http requests such as:
  //    A. Request type
  //    B. User agent
  //    C. Content type
  //    D. Content length
  //    E. Ping server port (Content server's)
  //    F. Lamport clock
  // 2. Content: (String) The content in the http request itself.
  // 3. Content lines: Number of lines in the content .
  public static HttpBundle parseHttpRequest(String request) {
    HashMap<String, String> headerCategoryMap = new HashMap<>();

    // Put each line in the string into a vector.
    String[] lines = request.split("\\r?\\n");
    HttpHeader httpHeader = new HttpHeader();

    System.out.println("Request string: " + request);

    httpHeader.requestType = lines[0].substring(0, 3);

    Integer lineIndex;
    // Split the header in to key and values where key is the substring to the left of the colon
    // and the value is the substring to the right o the colon with any preceding whitespace trimmed.
    // The key and values pair are stored in a map for easier retrieval.
    for (lineIndex = 1; lineIndex < lines.length; lineIndex++) {
      String line = lines[lineIndex];
      Integer colonIndex = line.indexOf(':');
      if (colonIndex == -1) {
        break;
      }
      System.out.println(line.substring(0, colonIndex) + " = " + line.substring(colonIndex + 1).replaceAll("\\s", ""));
      headerCategoryMap.put(line.substring(0, colonIndex), line.substring(colonIndex + 1).replaceAll("\\s", ""));
    }

    // Check if the header contains any of the key below. And put the information into the HttpHeader if it does.
    httpHeader.userAgent = headerCategoryMap.get("User-Agent");
    httpHeader.contentType = headerCategoryMap.get("Content-Type");
    try {
      httpHeader.contentLength = Integer.parseInt(headerCategoryMap.get("Content-Length"));
    } catch (Exception exception) {
      httpHeader.contentLength = null;
    }
    try {
      httpHeader.pingServerPort = Integer.parseInt(headerCategoryMap.get("PingServer-Port-Number"));
    } catch (Exception exception) {
      httpHeader.contentLength = null;
    }
    try {
      httpHeader.clock = Integer.parseInt(headerCategoryMap.get("Clock"));
    } catch (Exception exception) {
      httpHeader.clock = null;
    }

    // Append the content into a single strin
    String content = "";
    Integer contentLines = 0;
    for (lineIndex -= 1; lineIndex < lines.length; lineIndex++) {
      content += lines[lineIndex] + "\r\n";
      contentLines++;
    }
    if (httpHeader.requestType.equals("PUT") && (httpHeader.userAgent == null || httpHeader.contentType == null
        || httpHeader.contentLength == null || httpHeader.requestType == null)) {
      return new HttpBundle();
    }
    // System.out.println("Content =========== " + content);
    return new HttpBundle(httpHeader, content, contentLines);
  }
}
