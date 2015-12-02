/*
 * Created on 29 Feb 2008
 */
package uk.org.ponder.rsac.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration()
public class TestRSAC extends AbstractRSACTests {

  public TestRSAC() {
    contributeRequestConfigLocation("classpath:uk/org/ponder/rsac/test/rsac-request-context.xml");
    contributeConfigLocation("classpath:conf/core-rsac-context.xml");
  }

  @Test
  public void testRSAC() {
    Object obj = locateRequestBean("nullTest");
    
    Assert.assertTrue(obj instanceof NullHolder);
  }
}
