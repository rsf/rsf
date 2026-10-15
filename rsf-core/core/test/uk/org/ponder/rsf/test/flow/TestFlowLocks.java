package uk.org.ponder.rsf.test.flow;

import org.junit.Test;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import uk.org.ponder.rsf.flow.FlowIDHolder;
import uk.org.ponder.rsf.flow.support.BasicScopedAlterationWrapper;
import uk.org.ponder.rsf.state.support.InMemoryLockGetter;
import uk.org.ponder.stringutil.StringList;

public class TestFlowLocks {
  @Test
  public void waitingRequestsKeepTheSameMonitor() {
    InMemoryLockGetter locks = new InMemoryLockGetter();
    Object holder = locks.getLock("flow");
    Object waiter = locks.getLock("flow");
    assertSame(holder, waiter);
    locks.returnLock("flow");
    Object newcomer = locks.getLock("flow");
    assertSame(waiter, newcomer);
    locks.returnLock("flow");
    locks.returnLock("flow");
    Object fresh = locks.getLock("flow");
    assertNotSame(holder, fresh);
    locks.returnLock("flow");
  }

  @Test
  public void failingActionsReleaseBothFlowAndScopeLocks() {
    InMemoryLockGetter locks = new InMemoryLockGetter();
    BasicScopedAlterationWrapper wrapper = new BasicScopedAlterationWrapper();
    FlowIDHolder flow = new FlowIDHolder();
    flow.setFlowToken("flow");
    StringList scopes = new StringList();
    scopes.add("scope");
    wrapper.setLockGetter(locks);
    wrapper.setFlowIDHolder(flow);
    wrapper.setScopeLocks(scopes);
    Object flowMonitor = locks.getLock("flow");
    Object scopeMonitor = locks.getLock("scope");
    IllegalStateException failure = new IllegalStateException("Action failed");
    try {
      wrapper.invokeRunnable(() -> { throw failure; });
      fail("Expected action failure");
    }
    catch (IllegalStateException e) {
      assertSame(failure, e);
    }
    locks.returnLock("flow");
    locks.returnLock("scope");
    Object newFlow = locks.getLock("flow");
    Object newScope = locks.getLock("scope");
    assertNotSame(flowMonitor, newFlow);
    assertNotSame(scopeMonitor, newScope);
    locks.returnLock("flow");
    locks.returnLock("scope");
  }
}
