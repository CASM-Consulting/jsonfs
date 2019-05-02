package uk.ac.susx.jsonfs;

public class UnsupportedType extends Exception {

    public UnsupportedType() {
        super();
    }
    public UnsupportedType(String msg) {
        super(msg);
    }

    public UnsupportedType(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnsupportedType(Throwable cause) {
        super(cause);
    }
}
