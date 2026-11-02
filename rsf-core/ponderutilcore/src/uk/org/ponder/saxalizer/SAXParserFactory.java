package uk.org.ponder.saxalizer;

import javax.xml.XMLConstants;

import org.xml.sax.Parser;

import uk.org.ponder.util.UniversalRuntimeException;

/** Creates SAX parsers without permission to access external resources.
 * Registered local entity resolvers may still supply DTDs as streams.
 */
public class SAXParserFactory {
  public static Parser newParser() {
    try {
      // Sakai's Xerces 2.12.2 provider rejects these JAXP access restrictions.
      javax.xml.parsers.SAXParserFactory factory =
          javax.xml.parsers.SAXParserFactory.newDefaultInstance();
      factory.setValidating(false);
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      javax.xml.parsers.SAXParser parser = factory.newSAXParser();
      parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      return parser.getParser();
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Unable to create secure SAX parser");
    }
  }
}
