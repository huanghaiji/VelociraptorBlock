package velociraptor.executor.tag;

import velociraptor.block.Block;

public interface ISG {

    /**
     * block的id
     */
    long getBlockId();

    /**
     * block的char id
     */
    String getBlockCharId();


    /**
     * 接收来自服务器的block，或者程序的block
     */
    void onRev(Block block);

}
