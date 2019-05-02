package uk.ac.susx.jsonfs;

public interface Serialiser {

    <T> String serialise(T original) throws UnsupportedType;
    <T> T deserialise(String serialised) throws UnsupportedType;
}
