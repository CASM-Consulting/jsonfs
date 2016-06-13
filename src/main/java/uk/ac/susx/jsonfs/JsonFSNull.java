package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSNull extends JsonFSPrimitiveEntry<Void> {

    public JsonFSNull(Path path) {
        super(path);
    }

    public JsonFSNull(Path path, Void v) {
        super(path, Type.NULL, null);
    }

    @Override
    void value(Void value) {

    }

    @Override
    Void value() {
        return null;
    }
}
