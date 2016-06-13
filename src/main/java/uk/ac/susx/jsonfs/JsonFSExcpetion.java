package uk.ac.susx.jsonfs;

/**
 * Created by sw206 on 10/06/2016.
 */
public class JsonFSExcpetion extends RuntimeException {
    public JsonFSExcpetion(){
        super();
    }
    public JsonFSExcpetion(String s) {
        super(s);
    }
    public JsonFSExcpetion(Throwable t) {
        super(t);
    }
    public JsonFSExcpetion(String msg, Throwable t){
        super(msg, t);
    }
}
