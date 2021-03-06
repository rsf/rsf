/*
 * Created on Nov 1, 2005
 */
package uk.org.ponder.rsf.componentprocessor;

import java.util.HashMap;
import java.util.Map;

import uk.org.ponder.rsf.components.UIComponent;
import uk.org.ponder.rsf.components.UIForm;

public class BasicFormModel implements FormModel {
  // this is a map of component IDs in this container, to their parent
  // form.
  private Map componentToForm = new HashMap();
  
  public void registerChild(UIForm parent, UIComponent submitting) {
    componentToForm.put(submitting, parent);
  }
    
  public UIForm formForComponent(UIComponent component) {
    return (UIForm) componentToForm.get(component);
  }
  
  // For CONTAINMENT forms, we can render fossilized fields alongside their
  // owners. For EXTERNAL (WAP) forms, it MUST be that the submitting controls
  // FOLLOW their targets in the rendered file, otherwise we cannot determine
  // whether a control will be suppressed or not.
  // HENCE: for CONTAINMENT forms, each individual BOUND renders the fossil
  // alongside itself. For EXTERNAL forms, we have the problem of knowing
  // whether something has been rendererd, but it is easy to "find" the
  // ID of the component fossil. IS IT?? Well, we can deal with that when 
  // it comes.
}
