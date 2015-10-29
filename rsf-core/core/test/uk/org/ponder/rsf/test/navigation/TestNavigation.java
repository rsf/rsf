/*
 * Created on 8 Jan 2008
 */
package uk.org.ponder.rsf.test.navigation;

import org.junit.Assert;

import uk.org.ponder.rsf.bare.ActionResponse;
import uk.org.ponder.rsf.bare.RenderResponse;
import uk.org.ponder.rsf.bare.junit.MultipleRSFTests;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.util.Constants;

/** Test for encoding of null values, NavigationCase fallback rules and other RSF EL oddities
 */


public class TestNavigation extends MultipleRSFTests {
  
  public TestNavigation() {
    contributeRequestConfigLocation("classpath:uk/org/ponder/rsf/test/navigation/navigation-request-context.xml");
    contributeConfigLocation("classpath:uk/org/ponder/rsf/test/navigation/navigation-application-context.xml");
  }
   
  public void testRender() {
	Assert.assertEquals("default", submitForm("nullMethod"));
	Assert.assertEquals("first", submitForm("stringMethod.'first'"));
	Assert.assertEquals("default", submitForm("stringMethod.'" + Constants.NULL_STRING + "'"));
	Assert.assertEquals("default", submitForm("stringMethod.'third'"));
	Assert.assertEquals("third", submitForm("stringMethod.'second'"));
  }

  private String submitForm(String el) {
    RenderResponse render = getRequestLauncher().renderView(new ELParams("actionBean." + el));
    UIForm form = (UIForm) render.viewWrapper.queryComponent(new UIForm());
    UICommand command = (UICommand) render.viewWrapper.queryComponent(new UICommand());
    ActionResponse response = getRequestLauncher().submitForm(form, command);
    assertActionError(response, false);
    SimpleViewParameters resulting = (SimpleViewParameters) response.ARIResult.resultingView;
    return resulting.viewID;
  }
}
