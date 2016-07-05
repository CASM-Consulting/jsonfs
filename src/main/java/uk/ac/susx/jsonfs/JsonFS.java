package uk.ac.susx.jsonfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

    public JsonFSEntry<?> get(){
        return JsonFSEntry.get(root);
    }

}
