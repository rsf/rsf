/*
 * Created on 8 Jan 2008
 */
package uk.org.ponder.rsf.test.badtree;

import org.junit.Assert;

import uk.org.ponder.rsf.bare.RenderResponse;
import uk.org.ponder.rsf.bare.junit.MultipleRSFTests;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/** Test for various "bad tree" issues - 
 */

public class TestBadTree extends MultipleRSFTests {
  
  public TestBadTree() {
    contributeRequestConfigLocation("classpath:uk/org/ponder/rsf/test/badtree/badtree-request-context.xml");
    contributeConfigLocation("classpath:uk/org/ponder/rsf/test/badtree/badtree-application-context.xml");
  }
  
  public void testBadTree() {
    // Test for http://www.caret.cam.ac.uk/jira/browse/RSF-94
    RenderResponse render = getRequestLauncher().renderView();
    assertRenderError(render, true);
    
    RecordingFatalErrorHandler handler = (RecordingFatalErrorHandler) this.applicationContext.getBean("fatalErrorHandler");
    Assert.assertFalse(handler.error instanceof StackOverflowError);
  }
  
  public void testBadTree2() {
    // Test for http://www.caret.cam.ac.uk/jira/browse/RSF-39
    RenderResponse render = getRequestLauncher().renderView(new SimpleViewParameters(TestProducer2.VIEW_ID));
    assertRenderError(render, true);
  }
}
