/*
 * Created on 6 Dec 2015
 */
package uk.org.ponder.springutil;

import org.springframework.context.support.GenericApplicationContext;

/** A replacement for the use of plain GenericApplicationContext for operating the 
 *  RSAC context, since the Spring 4.x incarnation now obnoxiously throws exceptions
 *  when accessing beans before it declares itself "ready".
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class GenericAndReasonableApplicationContext extends
    GenericApplicationContext {
  protected void assertBeanFactoryActive() {
  }
}
