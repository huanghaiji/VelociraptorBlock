package velociraptor.block;

import java.io.IOException;

public interface BlockFullReader {

    void push(byte[] bytes) throws IOException;

    Block readBlock();

}
