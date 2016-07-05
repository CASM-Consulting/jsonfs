package uk.ac.susx.jsonfs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by sw206 on 13/06/2016.
 */
public class JsonFSTest {

    JsonFS fs;
    JsonFSEntry<?> var;

    @Before
    public void setup() throws IOException {
        fs = new JsonFS("test");
    }

    @After
    public void tearDown() {
        var.delete();
    }

    @Test
    public void TestStr() throws Exception {
        var = fs.str("hello");
        assertEquals(var, fs.get());
        assertEquals("hello", var.value());
    }
    @Test
    public void TestLong() throws Exception {
        var = fs.lng(1);
        assertEquals(var, fs.get());
        assertEquals(1l, var.value());
    }
    @Test
    public void TestDouble() throws Exception {
        var = fs.dbl(3.4);
        assertEquals(var, fs.get());
        assertEquals(3.4, var.value());
    }
    @Test
    public void TestBoolean() throws Exception {
        var = fs.bool(false);
        assertEquals(var, fs.get());
        assertEquals(false, var.value());
    }

    @Test
    public void TestObject() throws Exception {
        Map map = new HashMap<>();
        map.put("hello", "world");
        map.put("one", Arrays.asList(1l, 2l, "three"));
        map.put("five", null);
        JsonFSObject obj = fs.object(map);
        var = obj;

        assertEquals(var, fs.get());
        assertEquals(map, var.value());

        obj.remove("hello");

        assertEquals(var, fs.get());

        obj.put("one", null);
    }



    @Test
    public void TestArray() throws Exception {
        List list = new ArrayList<>();
        Map map = new HashMap<>();
        map.put("hello", "world");
        map.put("one", Arrays.asList(1l, 2l, "three"));
        list.add("1");
        list.add(map);
        JsonFSArray array = fs.array(list);
        var = array;

        assertEquals(var, fs.get());
        assertEquals(list, var.value());

        array.remove(map);

        assertEquals(var, fs.get());
    }

    @Test
    public void TestKeyPath() {

        List list = new ArrayList<>();
        Map map = new HashMap<>();
        map.put("one", Arrays.asList(1l, 2l, "three"));
        list.add(map);

        JsonFSArray object = fs.array(list);
        var = object;

        assertEquals("three", object.get(0,"one",2));
    }

}