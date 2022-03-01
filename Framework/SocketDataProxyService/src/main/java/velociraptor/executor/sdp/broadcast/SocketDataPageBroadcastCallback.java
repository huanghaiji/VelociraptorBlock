package velociraptor.executor.sdp.broadcast;

import velociraptor.block.Block;
import velociraptor.so.top.role.BlockUser;

public interface SocketDataPageBroadcastCallback {

    void onRev(BlockUser user, Block block,String location, SocketDataPageBroadcast broadcast);

}
