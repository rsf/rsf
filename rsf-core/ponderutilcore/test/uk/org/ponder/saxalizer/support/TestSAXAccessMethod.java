package uk.org.ponder.saxalizer.support;


import junit.framework.TestCase;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;


public class TestSAXAccessMethod extends TestCase {

    public void testMultipleSetters() {
        // Check that when there are multiple setters the found method is consistent.
        // It sorts the parameters classes as string so will prefer one nearer the start of the alphabet
        {
            SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
            spec.setmethodname = "setValue";
            SAXAccessMethod accessMethod = new SAXAccessMethod(spec, TestClass.class);
            assertEquals(Object.class, accessMethod.setmethod.getParameterTypes()[0]);
        }
        {
            SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
            spec.setmethodname = "setOther";
            SAXAccessMethod accessMethod = new SAXAccessMethod(spec, TestClass.class);
            assertEquals(Boolean.class, accessMethod.setmethod.getParameterTypes()[0]);
        }
        {
            SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
            spec.setmethodname = "setThing";
            SAXAccessMethod accessMethod = new SAXAccessMethod(spec, TestClass.class);
            assertEquals(Float.class, accessMethod.setmethod.getParameterTypes()[0]);
        }
        {
            SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
            spec.setmethodname = "setThat";
            SAXAccessMethod accessMethod = new SAXAccessMethod(spec, TestClass.class);
            assertEquals(Boolean.class, accessMethod.setmethod.getParameterTypes()[0]);
        }
    }

    public class TestClass {

        public void setValue(Object val) {}
        public void setValue(String val) {}

        public void setOther(Object val) {}
        public void setOther(Boolean val) {}

        public void setThing(Float val) {}
        public void setThing(Object val) {}

        public void setThat(Boolean val) {}
        public void setThat(Float val) {}
        public void setThat(Integer val) {}
        public void setThat(Object val) {}
        public void setThat(String val) {}


    }
}
