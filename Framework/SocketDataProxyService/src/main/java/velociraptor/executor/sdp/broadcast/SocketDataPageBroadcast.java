package velociraptor.executor.sdp.broadcast;

import velociraptor.block.Block;
import velociraptor.executor.sdp.SocketDataServer;
import velociraptor.so.top.role.BlockUser;

/**
 * 对于location进行监听来自服务器的数据。如果location存在具体处理的的proxy，那么数据不会再被此监听到。【2022-2-19日】
 */
public interface SocketDataPageBroadcast {

    void onStart(String location);

    void onStop();

    void onRev(BlockUser user, Block block,String location);

    SocketDataServer server();
}
