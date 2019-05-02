package uk.ac.susx.jsonfs;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class JsonFSAbstractCollection<T> extends JsonFSEntry<T> {

    private final Serialiser serialiser;

    public JsonFSAbstractCollection(Path path) {
        super(path);
        serialiser = null;
    }

    public JsonFSAbstractCollection(Path path, Type type, T value) {
        super(path, type, value);
        serialiser = null;
    }

    public JsonFSAbstractCollection(Path path, Type type, T value, Serialiser serialiser) {
        super(path, type, value);
        this.serialiser = serialiser;
    }

    public JsonFSEntry<T> get(Path path) {

        Type type = data(path.resolve(TYPE_FILE), Type::valueOf, (t)->t, LockOption.READ);

        JsonFSEntry<T> entry;

        switch (type) {
            case STRING:
                entry = (JsonFSEntry)new JsonFSString(path);
                break;
            case BOOLEAN:
                entry = (JsonFSEntry)new JsonFSBoolean(path);
                break;
            case DOUBLE:
                entry = (JsonFSEntry)new JsonFSDouble(path);
                break;
            case LONG:
                entry = (JsonFSEntry)new JsonFSLong(path);
                break;
            case NULL:
                entry = (JsonFSEntry)new JsonFSNull(path);
                break;
            case ARRAY:
                entry = (JsonFSEntry)new JsonFSArray(path);
                break;
            case OBJECT:
            entry = (JsonFSEntry)new JsonFSObject(path);
                break;
            default:
                throw new JsonFSExcpetion("null");
        }

        return entry;

    }

    public static JsonFSEntry<?> make(Path path, Object value) {

        if (value instanceof Number) {
            if (((Number) value).longValue() == ((Number) value).doubleValue()) {
                value = ((Number) value).longValue();
            }
        }

        JsonFSEntry<?> entry;

        if (value == null){
            entry = new JsonFSNull(path, null);
        } else {

            Class<?> cls = value.getClass();

            if(String.class.isAssignableFrom(cls)) {

                entry = new JsonFSString(path, (String)value);

            } else if(Long.class.isAssignableFrom(cls)) {

                entry = new JsonFSLong(path, (long)value);

            }  else if(Double.class.isAssignableFrom(cls)) {

                entry = new JsonFSDouble(path, (double)value);

            } else if(Boolean.class.isAssignableFrom(cls)) {

                entry = new JsonFSBoolean(path, (boolean)value);

            } else if(List.class.isAssignableFrom(cls)) {

                entry = new JsonFSArray(path, (List)value);

            } else if(Map.class.isAssignableFrom(cls)) {

                entry = new JsonFSObject(path, (Map)value);
            } else {

                throw new JsonFSExcpetion("unsupported type " + cls.getName());
            }
        }


        return entry;
    }
}
