/*
 * Created on 21 Nov 2006
 */
package uk.org.ponder.rsac.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HttpServletFactory {

  public void setHttpServletRequest(HttpServletRequest request);

  public HttpServletRequest getHttpServletRequest();

  public void setHttpServletResponse(HttpServletResponse response);

  public HttpServletResponse getHttpServletResponse();
}