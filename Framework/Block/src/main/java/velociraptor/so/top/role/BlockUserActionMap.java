package velociraptor.so.top.role;

import velociraptor.block.BlockAction;

public interface BlockUserActionMap {

    /**
     * key = BlockTarget.value | .values.
     */
    void pushAction(BlockAction<BlockUser> action);

    void addAction(String k, BlockAction<BlockUser> v);

    BlockAction<BlockUser> get(String key);

    void removeAction(String k);

}
