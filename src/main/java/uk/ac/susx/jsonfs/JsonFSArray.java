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
    void value(List value) {
        lock(path.resolve(VALUE_FILE), channel->{

            for(int i = 0 ; i < value.size(); ++i) {
                add(i, value.get(i));
            }
            return value;
        });
    }

    @Override
    List value() {
        return (List)lock(path.resolve(VALUE_FILE), channel->{
            List list = new ArrayList<>();
            for(int i = 0; i < size(); ++i){
                list.add(get(i));
            }
            return list;
        });
    }


    @Override
    void delete() {
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
        int i = size();

        add(i, value);

        return true;
    }


    @Override
    public boolean remove(Object o) {

        return remove(indexOf(o))!=null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return value().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        for(Object item : c) {
            add(item);
        }
        return c.size()>0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        for(Object item : c) {
            add(index++, item);
        }
        return c.size()>0;
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

        Path valPath = path.resolve(Integer.toString(i));

        return get(valPath).value();
    }

    @Override
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public void add(int i, Object value) {
        Path valPath = path.resolve(Integer.toString(i));

        try {
            Files.createDirectories(valPath);
        } catch (IOException e ){
            throw new JsonFSExcpetion(e);
        }

        make(valPath, value);
    }

    @Override
    public Object remove(int i) {
        Object prev = get(i);

        JsonFSUtil.deleteFileOrFolder(path.resolve(Integer.toString(i)));

        return prev;
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
