package velociraptor.so.bot.block;

import velociraptor.block.Block;
import velociraptor.so.bot.SSIOExceptionBlock;

import static velociraptor.so.top.target.SoTarget.SOCKET_IO_EXCEPTION;


public class SocketIOExceptionBlock implements SSIOExceptionBlock {

    @Override
    public Block exception(Block block) {
        block.setTarget(SOCKET_IO_EXCEPTION);
        block.setDataUTF8(MESSAGE);
        return block;
    }

}