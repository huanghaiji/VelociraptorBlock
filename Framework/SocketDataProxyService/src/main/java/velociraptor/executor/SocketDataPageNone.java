package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.executor.tag.ISG;
import velociraptor.protocol.BlockData;
import velociraptor.so.top.role.BlockUser;

import static velociraptor.executor.SocketDataService.id;

/**
 * 仅发送数据，不接收数据的返回
 */
public class SocketDataPageNone implements ISG {

    long pid = id.incrementAndGet();
    private final BlockUser user;
    private String charId;

    public SocketDataPageNone(BlockUser user) {
        this.user = user;
        this.charId = user.getDeviceName();
    }
    public SocketDataPageNone(BlockUser user,String charId){
        this.user = user;
        this.charId = charId;
    }

    /**
     * 发送数据，但是不需要服务器返回数据
     */
    public void onSending(byte[] bytes, String location) {
        Block block = new BlockData(pid, charId, location);
        block.setData(bytes);
        user.appendFlowBlock(block);
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
    }

}
