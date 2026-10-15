package uk.org.ponder.rsac.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.rsac.servlet.RSACUtils;
import uk.org.ponder.rsac.test.AbstractRSACTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestRequestLifecycle extends AbstractRSACTests {
  public TestRequestLifecycle() {
    contributeConfigLocation("classpath:conf/core-rsac-context.xml");
    contributeRequestConfigLocation("classpath:uk/org/ponder/rsac/support/lifecycle-request-context.xml");
  }

  public static class RecordingSeed {
    public String value;
    public List<String> values;

    public void record() {
      values.add(value);
    }
  }

  @Test
  public void continuationsKeepIndependentSeedsAndRequestsReleaseThem() {
    RSACBeanLocatorImpl rsac = (RSACBeanLocatorImpl) getRSACBeanLocator();
    RSACUtils.startServletRequest(new MockHttpServletRequest(),
        new MockHttpServletResponse(), rsac, "httpServletFactory");
    WriteableBeanLocator beans = rsac.getBeanLocator();
    List<String> values = new ArrayList<>();
    beans.set("seed", "original");
    beans.set("recordedValues", values);
    RSACLazarusList continuations = (RSACLazarusList) beans.locateBean("RSACLazarusList");
    continuations.queueRunnable(continuations.getLazarusRunnable(
        Collections.singletonMap("seed", "first"), "recordSeed"));
    continuations.queueRunnable(continuations.getLazarusRunnable(
        Collections.emptyMap(), "recordSeed"));
    rsac.endRequest();
    try {
      assertEquals(Arrays.asList("first", "original"), values);
      assertTrue("Completed requests must not retain seed objects", rsac.getSeedMap().isEmpty());
    }
    finally {
      // The shared fixture closes the final request in tearDown.
      rsac.startRequest();
    }
    assertNull(rsac.getBeanLocator().locateBean("seed"));
  }
}
