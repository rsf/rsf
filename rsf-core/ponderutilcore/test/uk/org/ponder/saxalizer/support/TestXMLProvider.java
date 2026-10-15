package uk.org.ponder.saxalizer.support;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.util.UniversalRuntimeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestXMLProvider {
  public static class Value {
    public String text;
  }

  @Test
  public void readsOrdinaryXmlAndRejectsExternalEntities() {
    SAXalXMLProvider xml = new SAXalXMLProvider(SAXalizerMappingContext.instance());
    String external = "<!DOCTYPE value [<!ENTITY remote SYSTEM 'file:/rsf-test-must-not-be-read'>]>"
        + "<value><text>&remote;</text></value>";
    try {
      xml.readObject(Value.class, null, new StringReader(external));
      fail("External XML entities must be rejected");
    }
    catch (UniversalRuntimeException expected) {
      assertTrue(expected.getMessage().contains("not registered locally"));
    }
    Value value = (Value) xml.readObject(Value.class, null,
        new StringReader("<value><text>normal &amp; safe</text></value>"));
    assertEquals("normal & safe", value.text);
  }

  @Test
  public void keepsExplicitLocalDtdMappings() {
    SAXalXMLProvider xml = new SAXalXMLProvider(SAXalizerMappingContext.instance());
    LocalEntityResolver mapping = new LocalEntityResolver();
    mapping.setID("rsf-test-local");
    mapping.setPath("test.dtd");
    EntityResolverStash resolver = new EntityResolverStash();
    resolver.setEntityResolvers(List.of(mapping));
    resolver.setStreamResolver(path -> new ByteArrayInputStream(
        "<!ELEMENT value (text)><!ELEMENT text (#PCDATA)><!ENTITY local 'local value'>"
            .getBytes(StandardCharsets.UTF_8)));
    xml.setEntityResolverStash(resolver);
    Value value = (Value) xml.readObject(Value.class, null, new StringReader(
        "<!DOCTYPE value PUBLIC 'rsf-test-local' 'https://invalid.example/test.dtd'>"
            + "<value><text>&local;</text></value>"));
    assertEquals("local value", value.text);
  }
}
