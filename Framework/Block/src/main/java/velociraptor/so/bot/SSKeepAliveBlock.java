package velociraptor.so.bot;

import velociraptor.block.Block;

import static velociraptor.so.top.target.SoTarget.SOCKET_KEEP_ALIVE;

public interface SSKeepAliveBlock {

    default Block instanceKeepAliveBlock(Block block, String uid) {
        block.setCharId("KA");
        block.setId(Integer.parseInt(SOCKET_KEEP_ALIVE));
        block.setDataUTF8(uid);
        block.setTarget(SOCKET_KEEP_ALIVE);
        return block;
    }
}
