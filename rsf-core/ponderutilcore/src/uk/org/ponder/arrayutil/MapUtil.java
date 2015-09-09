/*
 * Created on 7 Aug 2006
 */
package uk.org.ponder.arrayutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Utilities for working with java.util.Map instances **/

public class MapUtil {

  public static void putMultiMap(Map target, Object key, Object value) {
    List oldlist = (List) target.get(key);
    if (oldlist == null) {
      oldlist = new ArrayList();
      target.put(key, oldlist);
    }
    oldlist.add(value);
  }
  
  /** 
   * Construct a Map consisting of the single supplied key/value pair
   * @param key the key
   * @param value the value
   * @return a new HashMap with one entry of key value 
   */
  public static Map make(Object key, Object value) {
    Map togo = new HashMap();
    togo.put(key, value);
    return togo;
  }
  
  /** 
   * Construct a Map holding the two supplied key/value pairs
   * @param key1 first key
   * @param value1 first value
   * @param key2 second key
   * @param value2 second value
   * @return A new HashMap containing the 2 keys and values
   */
public static Map make(Object key1, Object value1, Object key2, Object value2) {
    Map togo = new HashMap();
    togo.put(key1, value1);
    togo.put(key2, value2);
    return togo;
  }
}
