package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.executor.sdp.broadcast.SocketDataPageBroadcastCallback;
import velociraptor.executor.sdp.broadcast.SocketDataPageBroadcast;
import velociraptor.executor.sdp.SocketDataServer;
import velociraptor.so.top.role.BlockUser;

/**
 * 接收服务器的数据。
 */
public class SocketDataBroadcast  implements SocketDataPageBroadcast {

    private final SocketDataService server;
    private final BlockUser user;
    private final SocketDataPageBroadcastCallback callback;
    private SocketDataPageAction listening;

    public SocketDataBroadcast(SocketDataService server, BlockUser user, SocketDataPageBroadcastCallback callback) {
        this.server = server;
        this.user = user;
        this.callback = callback;
    }


    public void onStart(String location) {
        listening = server.checkBlockUserInterfaceLocation(user, location);
        listening.addBroadcast(this);
    }

    public void onStop(){
        listening.delBroadcast(this);
    }

    @Override
    public void onRev(BlockUser user, Block block, String location) {
        callback.onRev( user, block, location, this);
    }

    @Override
    public SocketDataServer server() {
        return server;
    }

}