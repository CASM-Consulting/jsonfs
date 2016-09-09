package uk.ac.susx.jsonfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Created by sw206 on 10/06/2016.
 */
abstract public class JsonFSPrimitiveEntry<T> extends JsonFSEntry<T> {

    public JsonFSPrimitiveEntry(Path path) {
        super(path);
    }

    protected JsonFSPrimitiveEntry(Path path, Type type, T value) {
        super(path, type, value);
    }

    protected T value(Function<String, T> converter) {
        return data(path.resolve(VALUE_FILE), converter, (v)->v, LockOption.READ);
    }

    protected void value(Function<String, T> converter, T val) {
        data(path.resolve(VALUE_FILE), converter, (v)->val, LockOption.WRITE);
    }
}
