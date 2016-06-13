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

    public JsonFS( Path root) throws IOException {
        Files.createDirectories(root);
        this.root = root;
    }

    public JsonFS(String root) throws IOException {
        this(Paths.get(root));
    }

    public JsonFSString str(String val) {
        return new JsonFSString(root, val);
    }

    public JsonFSBoolean bool(boolean val) {
        return new JsonFSBoolean(root, val);
    }

    public JsonFSDouble dbl(double val) {
        return new JsonFSDouble(root, val);
    }

    public JsonFSLong lng(long val) {
        return new JsonFSLong(root, val);
    }

    public JsonFSNull nul() {
        return new JsonFSNull(root, null);
    }

    public JsonFSObject object(Map val) {
        return new JsonFSObject(root, val);
    }

    public JsonFSArray array(List val) {
        return new JsonFSArray(root, val);
    }

    public JsonFSEntry<?> get(){
        return JsonFSEntry.get(root);
    }

}
