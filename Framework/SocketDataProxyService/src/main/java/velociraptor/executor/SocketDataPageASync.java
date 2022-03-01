package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.block.state.BlockState;
import velociraptor.block.state.BlockStateIterator;
import velociraptor.block.state.list.SSTate;
import velociraptor.executor.tag.IASync;
import velociraptor.protocol.BlockData;
import velociraptor.executor.sdp.SocketDataPage;
import velociraptor.executor.sdp.async.SocketDataRevCallback;
import velociraptor.so.top.role.BlockUser;

import static velociraptor.executor.SocketDataService.*;

public class SocketDataPageASync implements SocketDataPage, BlockState<BlockUser>, IASync {
    long pid = id.incrementAndGet();
    private final BlockUser user;
    private final SocketDataRevCallback callback;
    private final SocketDataService service;
    private final String charId;

    public SocketDataPageASync(SocketDataService service, BlockUser user, SocketDataRevCallback callback) {
        this(service, user, user.getDeviceName(), callback);
    }

    public SocketDataPageASync(SocketDataService service, BlockUser user, String charId, SocketDataRevCallback callback) {
        this.user = user;
        this.callback = callback;
        this.service = service;
        this.charId = charId;
    }

    @Override
    public SocketDataPageASync observeLocation(String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        return this;
    }

    /**
     * 发送数据，但是不发生阻塞
     */
    @Override
    public SocketDataPageASync onSending(byte[] bytes, String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        Block block = new BlockData(pid, charId, location);
        block.setData(bytes);
        user.appendFlowBlock(block);
        return this;
    }

    @Override
    public IASync onSending(String data, String charset, String location) {
        service.checkBlockUserInterfaceLocation(user, location);
        Block block = new BlockData(pid, charId,location);
        if (data!=null){
            if (charset ==null)block.setData( data.getBytes());
            else block.setData(data,charset);
        }
        user.appendFlowBlock(block);
        return this;
    }

    @Override
    public long getBlockId() {
        return pid;
    }

    @Override
    public String getBlockCharId() {
        return charId;
    }

    @Override
    public void onRev(Block block) {
        if (callback != null) callback.onRev(block == null ? null : block.getData(), this);
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
    public SocketDataService service() {
        return service;
    }

    @Override
    public void blockState(BlockUser u, BlockStateIterator<BlockUser> iterator, int state) {
        if (state == SSTate.CLOSE)
            service.commitAsync(this, null);
    }

}