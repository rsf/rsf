/*
 * Created on 15-Jan-2006
 */
package uk.org.ponder.rsf.state.support;

import java.util.HashMap;
import java.util.Map;

import uk.org.ponder.rsf.state.LockGetter;

/**
 * An application scope bean designed as a FlowLockGetter suitable for a
 * single-JVM application. Will cooperate, for example, with
 * {@link uk.org.ponder.rsf.flow.support.BasicScopedAlterationWrapper}
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */

public class InMemoryLockGetter implements LockGetter {
  // Count both holders and waiting requests before exposing a monitor.
  private final Map<String, LockEntry> flowlockmap = new HashMap<>();

  private static class LockEntry {
    int users;
  }

  public Object getLock(String flowtoken) {
    synchronized (flowlockmap) {
      LockEntry lock = flowlockmap.get(flowtoken);
      if (lock == null) {
        lock = new LockEntry();
        flowlockmap.put(flowtoken, lock);
      }
      ++lock.users;
      return lock;
    }
  }

  public void returnLock(String flowtoken) {
    synchronized (flowlockmap) {
      LockEntry lock = flowlockmap.get(flowtoken);
      if (lock != null && --lock.users == 0) {
        flowlockmap.remove(flowtoken);
      }
    }
  }
}
