package velociraptor.protocol;

import velociraptor.block.Block;
import velociraptor.block.BlockFullReader;
import velociraptor.scheduling.io.ObjectListOutputStream;
import velociraptor.scheduling.io.QueueByteFrame;
import velociraptor.scheduling.io.QueueByteOutputStream;

import java.io.IOException;

class BlockReader implements BlockFullReader {


    private final ObjectListOutputStream<byte[],Byte> mom = new QueueByteOutputStream<byte[],Byte>().instanceArrayList();

    private BlockData block;
    private int blockHeaderSize;
    private int blockLength;

    @Override
    public void push(byte[] data) throws IOException {
        mom.write(data);
    }

    @Override
    public Block readBlock() {

        if (block == null) {
            /*
             * 注意数据跟信息头之间以\n结束，信息头不能包含\n
             */
            int position = mom.indexOf(0, (byte)'\n');
            if (position == -1)
                return null;
            String[] header = new String(mom.get(mom.find(0, position))).split(",");
            block = new BlockData();
            block.id = Long.parseLong(header[0]);
            block.charId = header[1];
            block.size = Integer.parseInt(header[2]);
            block.target =  header[3];
            blockHeaderSize = position;
            blockLength = blockHeaderSize +1 + block.size;
        }

        if (mom.size() < blockLength) {
            return null;
        }

        QueueByteFrame bf = mom.find(blockHeaderSize +1, block.size);
        block.data =  mom.get(bf);
        mom.del( mom.find(0, blockLength));

        BlockData b = block;
        block = null;
        return b;

    }

}