package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSNull extends JsonFSPrimitiveEntry<Void> {

    public JsonFSNull(Path path) {
        super(path);
    }

    @Override
    void value(Void value) {

    }



    @Override
    Void value() {
        return null;
    }
}
