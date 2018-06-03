/*
 * BitObfuscatorInputStream.java
 *
 * Created on Sep 23, 2007, 8:58:56 PM
 *
 *
 *
 */

package info.heinzelnisse.he;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author hklein
 */
public class BitObfuscatorInputStream extends InputStream {

    InputStream is;

    public BitObfuscatorInputStream(InputStream is) {
        this.is = is;
    }

    public int read() throws IOException {
        int data = is.read();
        if (data == -1) {
            return data;
        }
        // switch some bits (1 and 8) of the byte containing the data
        int switch1_8 = ((data & 128) >> 7) + ((data & 1) << 7);
        data &= (255 - 128 - 1); // setting bit 8 and 1 to zero
        data |= switch1_8; // setting new 1 and 8
        return data;
    }
    
    @Override
    public void close() throws IOException {
        if (is != null) {
            is.close();
        }
    }
}