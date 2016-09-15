package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSString extends JsonFSPrimitiveEntry<String> {

    public JsonFSString(Path path, String value) {
        super(path, Type.STRING, value);
    }

    public JsonFSString(Path path) {
        super(path);
    }

    @Override
    public void value(String value) {
        value(String::toString, value);
    }


    @Override
    public String value() {
        return value(String::toString);
    }

}
