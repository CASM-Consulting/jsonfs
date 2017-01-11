package uk.ac.susx.jsonfs;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSObject extends JsonFSEntry<Map<String,Object>> implements Map<String, Object> {

    public JsonFSObject(Path path) {
        super(path);
     }

    public JsonFSObject(Path path, Map value) {
        super(path, Type.OBJECT, value);
    }


    private Path path(Object key) {
        return path.resolve(key.toString().replace("/", "\\\\"));
    }

    @Override
    public void value(Map<String,Object> value) {


        lock(path.resolve(VALUE_FILE), (c, l) -> {
            Set<String> cur = keySet();

            cur.removeAll(value.keySet());

            for(String remove : cur) {
                remove(remove);
            }

            for(Entry<String, Object> entry : value.entrySet()) {
                put(entry.getKey().replaceAll("/", "\\\\"), entry.getValue());
            }
            return value;
        }, LockOption.WRITE);

    }


    @Override
    public Map value() {

        return (Map)lock(path.resolve(VALUE_FILE), (c, l) -> {
            try {
                List<Path> paths = Files.walk(path, 1)
                        .filter(file->Files.isDirectory(file) && !file.equals(path))
                        .collect(Collectors.toList());

                Map<String,Object> map = new HashMap<>();
                for(Path path : paths) {
                    String key = path.getName(path.getNameCount()-1).toString().replaceAll("\\\\", "/");
                    Object val = get(path).value();
                    map.put(key, val);
                }


                return map;

            }catch (IOException e) {
                throw new JsonFSExcpetion(e);
            }
        }, LockOption.READ);
    }


    public void delete() {
        clear();
        super.delete();
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return lock(path.resolve(LOCK_FILE), (c,l) -> Files.exists(path(key)), LockOption.READ);
    }

    @Override
    public boolean containsValue(Object value) {
        return lock(path.resolve(LOCK_FILE), (c,l) -> value().containsValue(value), LockOption.READ);
    }

    @Override
    public Object get(Object key) {

        return lock(path.resolve(LOCK_FILE), (c, l)->{

            Path keyPath = path.resolve(key.toString().replace("/", "\\\\"));

            if(Files.exists(keyPath)) {
                JsonFSEntry entry = get(keyPath);
                return entry.type().equals(Type.NULL) ? null : entry.value();
            } else {
                return null;
            }

        }, LockOption.READ);
    }

    public Object get(final Object... keys) {

        return lock(path.resolve(LOCK_FILE), (c, l)->{

            if(keys.length > 1) {
                Object key = keys[0];
                Path keyPath = path.resolve(key.toString().replace("/", "\\\\"));

                JsonFSEntry<?> entry = get(keyPath);

                if(entry.type().equals(Type.OBJECT)) {
                    return ((JsonFSObject)entry).get(Arrays.copyOfRange(keys,1, keys.length));
                } else if(entry.type().equals(Type.ARRAY)) {
                    return ((JsonFSArray)entry).get(Arrays.copyOfRange(keys,1, keys.length));
                } else {
                    throw new JsonFSExcpetion(key + " does not hold an object.");
                }

            } else {
                return get(keys[0]);
            }
        }, LockOption.READ);
    }


    public JsonFSEntry getJson(Object key) {

        return (JsonFSEntry)lock(path.resolve(LOCK_FILE), (c, l)->{

            Path keyPath = path.resolve(key.toString().replace("/", "\\\\"));

            if(Files.exists(keyPath)) {
                JsonFSEntry entry = get(keyPath);
                return entry.type().equals(Type.NULL) ? null : entry;
            } else {
                return null;
            }

        }, LockOption.READ);
    }


    @Override
    public Object put(String key, Object value) {

        return lock(path.resolve(LOCK_FILE), (c, l)->{

            if(key.equals(VALUE_FILE) || key.equals(TYPE_FILE) || key.equals(LOCK_FILE)) {
                throw new UnsupportedOperationException(VALUE_FILE + " and " + TYPE_FILE + " and " + LOCK_FILE + " are not allowed as keys.");
            }

            Path keyPath = path(key);

            Object prev;
            try {

                prev = get(key);
            } catch (JsonFSExcpetion | NullPointerException e) {
                prev = null;
            }

            if( prev != null && prev.equals(value)) {

                return value;

            } else {

                try {
                    Files.createDirectories(keyPath);
                } catch (IOException e ){
                    throw new JsonFSExcpetion(e);
                }

                make(keyPath, value);
            }

            return prev;

        }, LockOption.READ);
    }

    @Override
    public Object remove(Object key) {
        return lock(path(key).resolve(LOCK_FILE), (c, l) -> {
            Object pre = get(key);
            if(pre != null) {

                JsonFSUtil.deleteFileOrFolder(path(key));
            }
            return pre;

        }, LockOption.WRITE);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {

    }

    @Override
    public void clear() {
        lock(path.resolve(LOCK_FILE), (c, l)->{
            try {
                Files.walk(path, 1)
                        .filter(file->Files.isDirectory(file) && !file.equals(path))
//                        .forEach(JsonFSUtil::deleteFileOrFolder);
                        .forEach(p->get(p).delete());
            } catch (IOException e) {
                //ignore
            }
            return null;
        }, LockOption.WRITE);
    }

    @Override
    public Set<String> keySet() {
        try {
            Set set = Files.walk(path, 1)
                    .filter(file->Files.isDirectory(file) && !file.equals(path))
                    .map(file->file.getName(file.getNameCount()-1).toString().replaceAll("\\\\", "/"))
                    .collect(Collectors.toSet());


            return set;

        }catch (IOException e) {
            throw new JsonFSExcpetion(e);
        }
    }

    @Override
    public Collection<Object> values() {
        return value().values();
    }


    @Override
    public Set<Entry<String, Object>> entrySet() {
        return value().entrySet();
    }
}
