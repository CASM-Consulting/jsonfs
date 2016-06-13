package uk.ac.susx.jsonfs;

import java.nio.file.Path;

/**
 * Created by sw206 on 10/06/2016.
 */
public class    JsonFSDouble extends JsonFSPrimitiveEntry<Double> {
    public JsonFSDouble(Path path) {
        super(path);
    }
    public JsonFSDouble(Path path, Double value) {
        super(path, Type.DOUBLE, value);
    }
    @Override
    void value(Double value) {
        value(Double::parseDouble, value);
    }


    @Override
    Double value() {
        return value(Double::parseDouble);
    }
}
