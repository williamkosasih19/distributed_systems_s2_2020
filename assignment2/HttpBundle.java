public class HttpBundle {
  private HttpHeader httpHeader;
  private String content;
  private Integer contentLines;
  private Boolean invalidRequest;
    
  public HttpBundle() {
    invalidRequest = true;
  }
      
  public HttpBundle(HttpHeader httpHeader, String content, Integer contentLines) {
    invalidRequest = false;
    this.httpHeader = httpHeader;
    this.content = content;
    this.contentLines = contentLines;
  }
  
  public HttpHeader getHttpHeader() {
    return httpHeader;
  }
  
  public String getContent() {
    return content;
  }
  
  public Boolean isInvalid() {
    return invalidRequest;
  }
  
  public Integer getContentLines() {
    return contentLines;
  }
  
}