package velociraptor.protocol;

import velociraptor.block.BlockFullWrite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class BlockWrite implements BlockFullWrite {

    @Override
    public byte[] full(velociraptor.block.Block block) {
        BlockData b = (BlockData) block;
        ByteArrayOutputStream output =new ByteArrayOutputStream();
        try {
            output.write((b.id+","+b.charId+","+b.size+","+b.target+"\n").getBytes());
            if (b.data!=null)
                output.write(b.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

}
