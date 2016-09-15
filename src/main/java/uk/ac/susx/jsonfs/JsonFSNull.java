package uk.ac.susx.jsonfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
    public void value(Void value) {
        data(path.resolve(VALUE_FILE), (String s)->(Void)null, (v)->v, LockOption.WRITE);
        try {
            Files.walk(path, 1)
                    .filter(file->Files.isDirectory(file) && !file.equals(path))
                    .forEach(JsonFSUtil::deleteFileOrFolder);

        } catch (IOException e) {
            throw new JsonFSExcpetion(e);
        }
    }

    @Override
    public Void value() {
        return null;
    }
}
