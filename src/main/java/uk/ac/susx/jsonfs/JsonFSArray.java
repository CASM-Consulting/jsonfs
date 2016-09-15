package uk.ac.susx.jsonfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSArray extends JsonFSEntry<List> implements List<Object> {

    public JsonFSArray(Path path) {
        super(path);
    }

    public JsonFSArray(Path path, List value) {
        super(path, Type.ARRAY, value);
    }

    @Override
    public void value(List value) {
        lock(path.resolve(VALUE_FILE), (c, l)->{
            clear();

            for(int i = 0 ; i < value.size(); ++i) {
                add(value.get(i));
            }
            return value;
        }, LockOption.WRITE);
    }

    @Override
    public List value() {
        return (List)lock(path.resolve(VALUE_FILE), (c, l)->{
            List list = new ArrayList<>();
            for(int i = 0; i < size(); ++i){
                list.add(get(i));
            }
            return list;
        }, LockOption.READ);
    }


    @Override
    public void delete() {
        clear();
        super.delete();
    }


    @Override
    public int size() {
        int i = 0;
        while(Files.exists(path.resolve(Integer.toString(i)))) {
            ++i;
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public boolean contains(Object o) {
        return value().contains(o);
    }


    @Override
    public Iterator<Object> iterator() {
        return value().iterator();
    }


    @Override
    public Object[] toArray() {
        return value().toArray();
    }


    @Override
    public <T> T[] toArray(T[] a) {
        return (T[])value().toArray(a);
    }

    @Override
    public boolean add(Object value) {
        return lock(path.resolve(LOCK_FILE), (c, l) ->{

            int i = size();

            set(i, value);

            return true;
        }, LockOption.WRITE);
    }


    @Override
    public boolean remove(Object o) {
        return lock(path.resolve(LOCK_FILE), (c, l)->{
            int idx = indexOf(o);
            if(idx == -1) {
                return false;
            } else {
                return remove(idx)!=null;
            }
        },LockOption.WRITE);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return value().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> col) {
        return lock(path.resolve(LOCK_FILE), (c, l)->{

            for (Object item : col) {
                add(item);
            }

            return c.size()>0;
        }, LockOption.WRITE);
    }

    @Override
    public boolean addAll(final int index, Collection<? extends Object> col) {
        return lock(path.resolve(LOCK_FILE), (c, l)->{

            int i = index;
            for (Object item : col) {
                add(i++, item);
            }
            l.lock();

            return c.size()>0;

        }, LockOption.WRITE);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        for(int i = size()-1; i >= 0; --i){
            remove(i);
        }
    }

    @Override
    public Object get(int i) {

        return lock(path.resolve(LOCK_FILE), (c, l)->{

            if(i < 0 || i >= size()) {
                throw new IndexOutOfBoundsException("index " + i + " is out of range.");
            }
            Path valPath = path.resolve(Integer.toString(i));

            return get(valPath).value();
        }, LockOption.READ);
    }


    public Object get(Object... keys) {

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
                return get((int)keys[0]);
            }
        }, LockOption.READ);
    }

    public JsonFSEntry getJson(int i) {

        return (JsonFSEntry)lock(path.resolve(LOCK_FILE), (c, l)->{

            Path keyPath = path.resolve(Integer.toString(i));

            if(Files.exists(keyPath)) {
                JsonFSEntry entry = get(keyPath);
                return entry.type().equals(Type.NULL) ? null : entry;
            } else {
                return null;
            }
        }, LockOption.READ);
    }

    @Override
    public Object set(int i, Object value) {
        return lock(path.resolve(LOCK_FILE), (c, l)->{

            Path valPath = path.resolve(Integer.toString(i));

            try {
                Files.createDirectories(valPath);
            } catch (IOException e ){
                throw new JsonFSExcpetion(e);
            }

            make(valPath, value);

            return null;

        }, LockOption.READ);
    }

    @Override
    public void add(int i, Object value) {
        lock(path.resolve(LOCK_FILE), (c, l)->{

            for(int j = size()-1; j >= i; --j){
                Path from = path.resolve(Integer.toString(j));
                Path to = path.resolve(Integer.toString(j+1));
                Files.move(from, to);
            }

            Path valPath = path.resolve(Integer.toString(i));

            try {
                Files.createDirectories(valPath);
            } catch (IOException e ){
                throw new JsonFSExcpetion(e);
            }

            make(valPath, value);

            return null;

        }, LockOption.WRITE);
    }

    @Override
    public Object remove(int i) {

        return lock(path.resolve(LOCK_FILE), (c, l)->{

            Object prev = get(i);

//            JsonFSUtil.deleteFileOrFolder(path.resolve(Integer.toString(i)));
            get(path.resolve(Integer.toString(i))).delete();

            return prev;
        }, LockOption.READ);
    }

    @Override
    public int indexOf(Object o) {
        return value().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return value().lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return value().listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return value().listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return value().subList(fromIndex, toIndex);
    }
}
