package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSLong extends JsonFSPrimitiveEntry<Long> {
    public JsonFSLong(Path path) {
        super(path);
    }
    public JsonFSLong(Path path, Long value) {
        super(path, Type.LONG, value);
    }
    @Override
    public void value(Long value) {
        value(Long::parseLong, value);
    }


    @Override
    public Long value() {
        return value(Long::parseLong);
    }
}
