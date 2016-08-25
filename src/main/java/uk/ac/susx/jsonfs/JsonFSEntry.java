package uk.ac.susx.jsonfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by sw206 on 10/06/2016.
 */
abstract public class JsonFSEntry<T> {

    protected static final Path TYPE_FILE = Paths.get("__type__");
    protected static Path VALUE_FILE = Paths.get("__value__");

    enum Type {
        STRING,
        LONG,
        DOUBLE,
        BOOLEAN,
        NULL,
        ARRAY,
        OBJECT
    }

    protected final Path path;

    public JsonFSEntry(Path path) {
        this.path = path;
    }

    protected JsonFSEntry(Path path, Type type, JsonFSEntry<T> value) {
        this(path,type, value.value());
    }

    protected JsonFSEntry(Path path, Type type, T value) {
        this.path = path;
        if(!Files.exists(path.resolve(TYPE_FILE))) {
            try {

                Files.createFile(path.resolve(TYPE_FILE));
            } catch (IOException e) {
                throw new JsonFSExcpetion(e);
            }
        }
        type(type);
        value(value);
    }

    abstract void value(T value);

    abstract T value();

    public void delete() {
//        try {
//            if(Files.exists(path.resolve(VALUE_FILE))){
//                Files.delete(path.resolve(VALUE_FILE));
//            }
//        } catch (IOException e) {
//            throw new JsonFSExcpetion(e);
//        }
//        try {
//            if(Files.exists(path.resolve(TYPE_FILE))){
//                Files.delete(path.resolve(TYPE_FILE));
//            }
//        } catch (IOException e) {
//            throw new JsonFSExcpetion(e);
//        }

        JsonFSUtil.deleteFileOrFolder(path);
    }

    protected void assertType(Type expected) {
        Type actual = type();
        if(!expected.equals(actual)) {
            throw new JsonFSExcpetion("trying to initialise a " +expected+ " on a " + actual);
        }
    }

    protected void type(Type type) {
        data(path.resolve(TYPE_FILE), Type::valueOf, (t)->type);
    }

    protected Type type() {
        return data(path.resolve(TYPE_FILE), Type::valueOf, (t)->t);
    }

    protected static <T> T data(Path path, Function<String, T> convert, Function<T, T> fn) {


        return lock(path, channel->{
            BufferedReader reader = new BufferedReader(Channels.newReader(channel, "UTF-8"));

            T val;
            try {
                val = convert.apply(reader.readLine());
            } catch (IllegalArgumentException | NullPointerException e) {
                val = null;
            }
            T result = fn.apply(val);

            if(result == null) {

                channel.truncate(0);

            } else if(!result.equals(val)) {

                channel.truncate(0);

                Writer out = Channels.newWriter(channel, "utf-8");
                out.write(result.toString());
                out.flush();

            }

            return result;
        });

    }

    protected static <T> T lock(Path path, FileChannelFn<T> fn) {
        try (
                FileChannel channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                FileLock ignored = channel.lock();
        ) {
            T result = fn.apply(channel);
            return result;

        } catch (IOException e){
            throw new JsonFSExcpetion(e);
        }
    }

    protected interface FileChannelFn<T> {

        T apply(FileChannel c) throws IOException;

    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if(JsonFSEntry.class.isAssignableFrom(other.getClass())
                &&  ((JsonFSEntry)other).value().equals(value())) {
            return true;
        } else {
            return false;
        }
    }

    public static JsonFSEntry<?> get(Path path) {

        Type type = data(path.resolve(TYPE_FILE), Type::valueOf, (t)->t);

        JsonFSEntry<?> entry;

        switch (type) {
            case STRING:
                entry = new JsonFSString(path);
                break;
            case BOOLEAN:
                entry = new JsonFSBoolean(path);
                break;
            case DOUBLE:
                entry = new JsonFSDouble(path);
                break;
            case LONG:
                entry = new JsonFSLong(path);
                break;
            case NULL:
                entry = new JsonFSNull(path);
                break;
            case ARRAY:
                entry = new JsonFSArray(path);
                break;
            case OBJECT:
                entry = new JsonFSObject(path);
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
