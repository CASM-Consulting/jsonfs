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
    void value(Map<String,Object> value) {


        lock(path.resolve(VALUE_FILE), channel -> {
            Set<String> cur = keySet();

            cur.removeAll(value.keySet());

            for(String remove : cur) {
                remove(remove);
            }

            for(Entry<String, Object> entry : value.entrySet()) {
                put(entry.getKey().replaceAll("/", "\\\\"), entry.getValue());
            }
            return value;
        });

    }


    @Override
    Map value() {

        return (Map)lock(path.resolve(VALUE_FILE), channel -> {
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
        });
    }


    void delete() {
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

        return Files.exists(path(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return value().containsValue(value);
    }

    @Override
    public Object get(Object key) {

        Path keyPath = path.resolve(key.toString().replace("/", "\\\\"));

        if(Files.exists(keyPath)) {
            JsonFSEntry entry = get(keyPath);
            return entry.type().equals(Type.NULL) ? null : entry.value();
        } else {
            return null;
        }

    }

    @Override
    public Object put(String key, Object value) {
        Path keyPath = path(key);

        Object prev;
        try {

            prev = get(key);
        } catch (JsonFSExcpetion | NullPointerException e) {
            prev = null;
        }


        try {
            Files.createDirectories(keyPath);
        } catch (IOException e ){
            throw new JsonFSExcpetion(e);
        }

        make(keyPath, value);


        return prev;
    }

    @Override
    public Object remove(Object key) {
        Object pre = get(key);
        if(pre != null) {

            JsonFSUtil.deleteFileOrFolder(path(key));
        }
        return pre;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {

    }

    @Override
    public void clear() {
        try {
            Files.walk(path, 1)
                    .filter(file->Files.isDirectory(file) && !file.equals(path))
                    .forEach(JsonFSUtil::deleteFileOrFolder);
        } catch (IOException e) {
            //ignore
        }
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
