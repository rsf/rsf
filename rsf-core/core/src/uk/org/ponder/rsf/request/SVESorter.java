/*
 * Created on Nov 9, 2005
 */
package uk.org.ponder.rsf.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import uk.org.ponder.arrayutil.MapUtil;
import uk.org.ponder.beanutil.PathUtil;

/** Applies a topological sorting to a collection of SubmittedValueEntry
 * objects, such that any read or write of an entry of the bean model is 
 * scheduled after a write of a dependency. A dependency of an EL operation
 * is a write to a path equal or higher to it in the bean hierarchy. Writes
 * to the same path will currently be scheduled arbitrarily.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * @author Andrew Thorton (andrew@three-tuns.net)
 *
 */
public class SVESorter {
// Notes on the topological sorting applied by this class.

//Each entry is a WRITE to the EL path "valuebinding".
//In addition, a "fast EL" may represent a "read" from path newvalue,
//if newvalue represents an EL.
//Component binding: will WRITE to valuebinding ( when real value appears )
//EL: will WRITE to valuebinding, READ from newvalue. 
//these READS must be shifted until AFTER writes of values related to newvalue.

//i) a WRITE to a nested path DEPENDS ON a WRITE to a STRICTLY higher path.
//ii) a READ from a nested path DEPENDS ON a WRITE to a STRICTLY OR EQUAL higher path.
//iii) a WRITE to the SAME PATH should technically just replace the dependent write.

//OK then. edge created FROM bean TO dependency, i.e. that which operates on higher path.
//                  WRITE                             READ
//e.g. valuebinding {permissionbean.name}       value [field]             NODE 1
//fast EL:          {resource.a8394.permission} value {permissionbean}    NODE 2
//edge FROM 2 -> 1
//hah. fast EL is actually SLOW!
  private RequestSubmittedValueCache rsvc;
  private ELDependencyMap elmap = new ELDependencyMap();
  private HashSet emitted = new HashSet();
  private ArrayList output = new ArrayList();
  private Map upstream = new HashMap();
  private Map downstream = new HashMap();

  public SVESorter(RequestSubmittedValueCache tosort) {
    this.rsvc = tosort;

    for (int i = 0; i < rsvc.getEntries(); ++i) {
      SubmittedValueEntry entry = rsvc.entryAt(i);
      elmap.recordWrite(entry.valuebinding, entry);
      if (entry.newvalue instanceof String && entry.isEL) {
        String readel = (String) entry.newvalue;
        if (readel != null) {
          elmap.recordRead(readel, entry);
        }
      }
    }
  }
// for a given EL write path, any initial component submission which gave rise to it
  public SubmittedValueEntry getUpstreamComponent(String writepath) {
    List writers = elmap.getWriters(writepath);
    
    if (writers == null) return null;

    for (int i = 0; i < writers.size(); ++ i) {
      SubmittedValueEntry writer = (SubmittedValueEntry) writers.get(i);
      if (writer.componentid != null) return writer;
      String readpath = elmap.getReadPath(writer);
      while (readpath != null) {
        SubmittedValueEntry sve = getUpstreamComponent(readpath);
        if (sve != null) return sve;
        readpath = PathUtil.getToTailPath(readpath);
      }
    }

    return null;
  }
  
  public Map getUpstreamMap() {
    return upstream;
  }
  
  public Map getDownstreamMap() {
    return downstream;
  }
  
  public List getSortedRSVC() {
    for (int i = 0; i < rsvc.getEntries(); ++i) {
      SubmittedValueEntry entry = rsvc.entryAt(i);
      String writepath = elmap.getWritePath(entry);
      SubmittedValueEntry upstream = getUpstreamComponent(writepath);
      if (upstream != null) {
        this.upstream.put(writepath, upstream);
        // NB, this is not sufficient to record dependencies amongst pure EL, but is
        // good enough for DateTransit for now.
        // If a given 
        MapUtil.putMultiMap(downstream, upstream.valuebinding, writepath);
      }
    
    }
    
    for (int i = 0; i < rsvc.getEntries(); ++i) {
      SubmittedValueEntry entry = rsvc.entryAt(i);
      if (!emitted.contains(entry)) {
        attemptEvaluate(entry);
      }
    }
    return output;
  }

  private void attemptEvaluate(SubmittedValueEntry entry) {
    emitted.add(entry);
    String readpath = elmap.getReadPath(entry);
    if (readpath != null) {
      scheduleWrites(readpath);
    }
    String writepath = elmap.getWritePath(entry);
    if (writepath != null) {
      scheduleWrites(writepath);
    }
    output.add(entry);
  }

  private void scheduleWrites(String writepath) {
    // NB, the keeping of this on the stack results from being SURE that we
    // can never recur to this path as a result of attemptEvaluate of
    // a parent path (Even though we clearly do this as a result of fearing
    // recurring to it from further up our *own* stack).
    List writingbeans = elmap.getWriters(writepath);
    if (writingbeans == ELDependencyMap.VALID_LIST_MARKER) return;
    // If we *write* a path, all pending *writes* to higher paths must already
    // be done.
    String parentpath = PathUtil.getToTailPath(writepath);
    if (parentpath != null) {
      scheduleWrites(parentpath);
    }
  
    if (writingbeans != null) {
      for (int i = 0; i < writingbeans.size(); ++i) {
        SubmittedValueEntry sve = (SubmittedValueEntry) writingbeans.get(i);
        if (!emitted.contains(sve)) {
          attemptEvaluate(sve);
        }
      }
      elmap.recordPathValid(writepath);
    }
  }
}
