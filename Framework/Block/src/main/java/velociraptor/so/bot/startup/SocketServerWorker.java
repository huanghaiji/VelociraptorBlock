package velociraptor.so.bot.startup;

import velociraptor.block.BlockAction;
import velociraptor.block.state.BlockState;
import velociraptor.so.bot.SSocket;
import velociraptor.so.top.role.BlockUser;


/**
 *  Server
 */
public interface SocketServerWorker extends SSocket {

    void pushBlockAction(BlockAction<BlockUser> action);

    void addBlockStateAction(BlockState<BlockUser> state);

    int port();

    default SocketServerWorker getInstance() {
        return this;
    }

    default void setCreateBlockUserCallback(CreateBlockUserCallback callback){

    }
    /**
     * 服务器创建use的监听回调
     */
    interface  CreateBlockUserCallback{
        void callback(BlockUser user);
    }
}
