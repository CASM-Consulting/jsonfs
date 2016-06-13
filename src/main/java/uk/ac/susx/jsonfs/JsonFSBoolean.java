package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSBoolean extends JsonFSPrimitiveEntry<Boolean> {
    public JsonFSBoolean(Path path) {
        super(path);
    }

    @Override
    void value(Boolean value) {
        value(Boolean::parseBoolean, value);
    }

    @Override
    Boolean value() {
        return value(Boolean::parseBoolean);
    }

    public JsonFSBoolean(Path path, Boolean value) {
        super(path, Type.BOOLEAN, value);
    }
}
