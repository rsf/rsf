/*
 * Created on Nov 23, 2005
 */
package uk.org.ponder.rsf.flow.lite;

import uk.org.ponder.rsf.util.XMLFactoryBean;

public class XMLFlowFactoryBean extends XMLFactoryBean {
  public XMLFlowFactoryBean() {
    setObjectType(Flow.class);
  }
  
  public Object getObject() throws Exception {
    Flow togo = (Flow) super.getObject();
    togo.init();
    return togo;
  }
}
