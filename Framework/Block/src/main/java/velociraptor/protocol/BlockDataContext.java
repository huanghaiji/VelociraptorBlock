package velociraptor.protocol;

import velociraptor.block.BlockContext;
import velociraptor.block.BlockFullReader;
import velociraptor.block.BlockFullWrite;

public class BlockDataContext implements BlockContext {

    private final BlockFullWrite write = new BlockWrite();
    private final BlockFullReader reader = new BlockReader();

    @Override
    public velociraptor.block.Block instanceBlock() {
        return new BlockData();
    }

    @Override
    public BlockFullWrite write() {
        return write;
    }

    @Override
    public BlockFullReader reader() {
        return reader;
    }
}
