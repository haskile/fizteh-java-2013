package ru.fizteh.fivt.students.piakovenko.filemap.storable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class DataBaseMap {
    private Map<String, Storeable> map = new HashMap<String, Storeable>(15);
    private Map<String, Storeable> changedMap = new HashMap<String, Storeable>(15);
    private Map<String, Storeable> overwriteMap = new HashMap<String, Storeable>(15);

    public Storeable put (String key, Storeable value) {
        Storeable oldValue = null;
        if (!map.containsKey(key)) {
            map.put(key, value);
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            oldValue = map.get(key);
            System.out.println(oldValue);
            map.remove(key);
            map.put(key, value);
        }
        return oldValue;
    }

    public Storeable get(String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(map.get(key));
            return map.get(key);
        }
        return null;
    }

    public Storeable remove (String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            Storeable returnValue = map.get(key);
            map.remove(key);
            System.out.println("removed");
            return returnValue;
        }
        return null;
    }

    public void primaryPut (String key, Storeable value) {
            map.put(key, value);
    }

    public Map<String, Storeable> getMap () {
        return map;
    }

    public void commit(Map<String, Storeable> _newMap, Map<String, Boolean> _removed) {
        for (final String removed: _removed.keySet()) {
            map.remove(removed);
        }
        for (final String added: _newMap.keySet()) {
            if (map.containsKey(added)) {
                map.remove(added);
            }
            map.put(added, _newMap.get(added));
        }
    }

}
