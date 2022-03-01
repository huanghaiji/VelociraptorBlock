package velociraptor.so.bot;

import velociraptor.block.Block;
import velociraptor.block.target.BlockBusiness;

import static velociraptor.so.top.target.SoTarget.SOCKET_IO_EXCEPTION;

@BlockBusiness(value = SOCKET_IO_EXCEPTION)
public interface SSIOExceptionBlock {

    String MESSAGE = "Block IO ERR";

    Block exception(Block block);

}