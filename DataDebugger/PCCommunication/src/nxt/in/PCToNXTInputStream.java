package nxt.in;

import java.io.IOException;
import java.io.InputStream;

public class PCToNXTInputStream{
    
    private InputStream in;
    
    public PCToNXTInputStream(InputStream source){
        this.in = source;
    }
    
    public int read() throws IOException{
        return in.read();
    }
    
    public void close() throws IOException{
        in.close(); //Die Methode "close" aus InputStream macht nichts....
    }
    
}
