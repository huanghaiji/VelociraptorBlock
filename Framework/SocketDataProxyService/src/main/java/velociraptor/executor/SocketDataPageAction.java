package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.block.BlockAction;
import velociraptor.executor.sdp.broadcast.SocketDataPageBroadcast;
import velociraptor.executor.tag.IASync;
import velociraptor.executor.tag.ISG;
import velociraptor.executor.tag.ISync;
import velociraptor.so.top.role.BlockUser;

import java.util.ArrayList;
import java.util.List;

class SocketDataPageAction implements BlockAction<BlockUser> {

    private final SocketDataService service;
    private final List<SocketDataPageBroadcast> broadcasts = new ArrayList<>();
    private final String location;


    synchronized void addBroadcast(SocketDataPageBroadcast callback){
        broadcasts.add(callback);
    }
    synchronized void delBroadcast(SocketDataPageBroadcast callback){
        broadcasts.remove(callback);
    }
    private synchronized void offerBroadcast(BlockUser user,Block block){
        if (!broadcasts.isEmpty())
        for (SocketDataPageBroadcast callback: broadcasts)
            callback.onRev(user, block, location);
    }

    SocketDataPageAction(SocketDataService service, String location) {
        this.service = service;
        this.location=location;
    }

    @Override
    public void analysis(BlockUser ctx, Block block) {
        ISG proxy = service.pagesGet(block.getId());
        if (proxy != null) {
            if (proxy instanceof ISync)
                proxy.onRev(block);
            else if (proxy instanceof IASync)
                service.commitAsync(proxy, block);
            return;
        }
        offerBroadcast(ctx, block);
    }

}