package uk.ac.susx.jsonfs;

import com.sun.management.UnixOperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFS {

    private final Path root;

    public JsonFS( Path root) throws JsonFSExcpetion {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new JsonFSExcpetion(e);
        }
        this.root = root;
    }

    public JsonFS(String root) throws JsonFSExcpetion {
        this(Paths.get(root));
    }

    public JsonFSString str(String val) {
        return new JsonFSString(root, val);
    }

    public JsonFSString str() {
        return new JsonFSString(root);
    }

    public JsonFSBoolean bool(boolean val) {
        return new JsonFSBoolean(root, val);
    }
    public JsonFSBoolean bool() {
        return new JsonFSBoolean(root);
    }

    public JsonFSDouble dbl(double val) {
        return new JsonFSDouble(root, val);
    }
    public JsonFSDouble dbl() {
        return new JsonFSDouble(root);
    }

    public JsonFSLong lng(long val) {
        return new JsonFSLong(root, val);
    }
    public JsonFSLong lng() {
        return new JsonFSLong(root);
    }

    public JsonFSNull nul(Void null_) {
        return new JsonFSNull(root, null);
    }

    public JsonFSNull nul() {
        return new JsonFSNull(root);
    }

    public JsonFSObject object(Map val) {
        return new JsonFSObject(root, val);
    }

    public JsonFSObject object() {
        return new JsonFSObject(root);
    }

    public JsonFSArray array(List val) {
        return new JsonFSArray(root, val);
    }

    public JsonFSArray array() {
        return new JsonFSArray(root);
    }

    public JsonFSEntry<?> get(){
        return JsonFSEntry.get(root);
    }

    public static void main(String[] args) {

        JsonFSObject obj = new JsonFS("test").object();

        obj.put("blah", new HashMap<>());

        Map val = obj.<JsonFSObject>getJson("blah");

        val.put("blah2", new HashMap<>());

        obj.put("blah", val);

        Map val2 = obj.<JsonFSObject>getJson("blah", "blah2");

        AtomicBoolean running = new AtomicBoolean(true);

        AtomicInteger n1 = new AtomicInteger(0);
        AtomicInteger n2 = new AtomicInteger(0);

        new Thread(()->{

            int i = 0;
            while(running.get()) {

                obj.put(i+"", i);

                if( i > 10) {
                    i = 0;
                }
                ++i;
//                System.out.println(n1.incrementAndGet());
//                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }

        }).start();

        new Thread(()->{

            int i = 0;
            while(running.get()) {

                obj.put(i+"", i);

                if( i > 10) {
                    i = 0;
                }
                ++i;
//                System.out.println(n2.incrementAndGet());
//                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }

        }).run();

//        Thread.sleep(5000);
//        running.set(false);
    }
}
