package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.block.state.BlockState;
import velociraptor.block.state.BlockStateIterator;
import velociraptor.block.state.list.SSTate;
import velociraptor.executor.sdp.SocketDataServer;
import velociraptor.executor.tag.BlockQueueSync;
import velociraptor.executor.tag.ISync;
import velociraptor.protocol.BlockData;
import velociraptor.executor.sdp.SocketDataPage;
import velociraptor.so.top.role.BlockUser;

import static velociraptor.executor.SocketDataService.*;

public class SocketDataPageSync extends BlockQueueSync implements SocketDataPage, BlockState<BlockUser> {
    long pid = id.incrementAndGet();
    private final BlockUser user;
    private final SocketDataService service;
    private final String charId;

    public SocketDataPageSync(BlockUser user, SocketDataService service) {
        this(user, service, user.getDeviceName());
    }

    public SocketDataPageSync(BlockUser user, SocketDataService service, String charId) {
        this.user = user;
        this.service = service;
        this.charId = charId;
    }

    @Override
    public SocketDataPageSync observeLocation(String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        return this;
    }


    public SocketDataPageSync onSending(byte[] bytes, String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        Block block = new BlockData(pid, charId, location);
        block.setData(bytes);
        user.appendFlowBlock(block);
        return this;
    }


    @Override
    public ISync onSending(String data, String charset, String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        Block block = new BlockData(pid, charId, location);
        if (data != null) {
            if (charset == null)
                block.setData(data.getBytes());
            else
                block.setData(data, charset);
        }
        user.appendFlowBlock(block);
        return this;
    }

    public void onRev(Block block) {
        offerToBlocks(block);
    }

    @Override
    public void onStart() {
        service.pagesAdd(pid, this);
        user.addBlockState(this);
    }

    @Override
    public void onStop() {
        service.pagesRemove(pid);
        user.delBlockState(this);
    }

    @Override
    public SocketDataServer service() {
        return service;
    }

    @Override
    public void blockState(BlockUser u, BlockStateIterator<BlockUser> iterator, int state) {
        if (state == SSTate.CLOSE || state == SSTate.IO_ERROR)
            useIsClose.set(true);
    }

    @Override
    public long getBlockId() {
        return pid;
    }

    @Override
    public String getBlockCharId() {
        return charId;
    }

}
