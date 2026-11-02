/*
 * Created on 05-Jan-2006
 */
package uk.org.ponder.rsf.state;

/** Acquires unique objects, which have identity equality equivalent to
 * String equality on the supplied String. 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface LockGetter {
  /** Reserves the monitor for a holder or waiter. Each call must be paired with
   * returnLock after leaving the synchronized block, including on failure.
   */
  public Object getLock(String lockname);
  public void returnLock(String lockname);
}
