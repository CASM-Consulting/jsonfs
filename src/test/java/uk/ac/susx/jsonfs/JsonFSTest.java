package uk.ac.susx.jsonfs;

import com.sun.management.UnixOperatingSystemMXBean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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


    @Test
    public void testLong() {

        Map<String, Integer> map = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            map.put("key"+i, i);
        }

        long tic = Instant.now().toEpochMilli();

        fs.object(map);

        long toc = Instant.now().toEpochMilli();

        System.out.println(toc-tic);

        tic = Instant.now().toEpochMilli();

        var = fs.object();

        toc = Instant.now().toEpochMilli();

        System.out.println(toc-tic);

    }

    @Test
    public void testDeep() {

        JsonFSObject obj = fs.object();
        for(int i = 0; i < 100; i++) {
            String key = "key"+i;
            obj.put(key, new HashMap());
            obj = (JsonFSObject)obj.getJson(key);
        }

        long tic = Instant.now().toEpochMilli();


        obj = fs.object();
        for(int i = 0; i < 100; i++) {
            String key = "key"+i;
            obj = (JsonFSObject)obj.getJson(key);
        }

        long toc = Instant.now().toEpochMilli();

        System.out.println(toc-tic);

        var = obj;
    }

    @Test
    public void concurrencyTest() throws InterruptedException {

        var = fs.object();

        JsonFSObject obj = (JsonFSObject)var;

        AtomicBoolean running = new AtomicBoolean(true);

        final int n = 10;

        Runnable runnable = ()->{
            int i = 0;
            while(running.get()) {
                obj.put("badgers", i);
                if( i > n) {
                    i = 0;
                }
                ++i;
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();


        Thread.sleep(1000);
        running.set(false);
        Thread.sleep(1000);
    }


    @Test
    public void concurrencyTest2() throws InterruptedException {

        var = fs.object();

        JsonFSObject obj = (JsonFSObject)var;

        AtomicBoolean running = new AtomicBoolean(true);

        final int n = 10;

        Runnable runnable = ()->{
            int i = 0;
            while(running.get()) {
                obj.put("badgers", i);
                if( i > n) {
                    i = 0;
                }
                ++i;
            }
        };

        new Thread(runnable).start();
        obj.remove("badgers");


//
//        Thread.sleep(1000);
//        running.set(false);
//        Thread.sleep(1000);
    }

    @Test
    public void arrayTestAdd() {

        var = fs.array(Arrays.asList("and", 1, 3));

        JsonFSArray array = (JsonFSArray)var;

        array.add(2, 2);

        Assert.assertEquals(2l, array.get(2));

        Assert.assertEquals("and", array.get(0));

        Assert.assertEquals(3l, array.get(3));

    }
}