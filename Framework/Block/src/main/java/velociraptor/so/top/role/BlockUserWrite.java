package velociraptor.so.top.role;

import velociraptor.block.BlockAction;
import velociraptor.block.Block;
import velociraptor.block.state.BlockState;
import velociraptor.block.state.BlockStateArray;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.scheduling.flow.FlowBlockQuantitativePackage;
import velociraptor.scheduling.flow.FlowBlockSourceControl;
import velociraptor.scheduling.interrupt.ErrorInterruptValue;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BlockUserWrite implements BlockUsr, FlowBlockQuantitativePackage, BlockStateArray<BlockUser>, ErrorInterruptValue {

    private final BlockUser user;
    private final FlowBlockQuantitative quantitative;
    protected Block FCLBlock;
    private final AtomicBoolean errorInterruptValue = new AtomicBoolean();
    //双控制：block user的nio流控制着等待响应，而block write user同样也有自己的回复信等待响应
    private final FlowBlockSourceControl ioFlow = new FlowBlockSourceControl() {
        @Override
        public Block instanceBlock() {
            return user.context().instanceBlock();
        }

        @Override
        public void analysis(Void ctx, Block block) {
            user.insertBlock(block);
        }

        @Override
        public FlowBlockQuantitative quantitative() {
            return quantitative;
        }


        @Override
        public Block FCLBlock() {
            return FCLBlock == null ? user.flow().FCLBlock() : FCLBlock;
        }

    };

    /**
     * @param quantitative 与 BlockUser 共享error中断。
     */
    public BlockUserWrite(BlockUser user, FlowBlockQuantitative quantitative) {
        this.user = user;
        this.quantitative = quantitative;
        this.quantitative.addErrorInterrupt(user);
        this.quantitative.addErrorInterrupt(this);
    }

    @Override
    public AtomicBoolean getMainErrorInterruptValue() {
        return errorInterruptValue;
    }

    @Override
    public FlowBlockQuantitative quantitative() {
        return quantitative;
    }

    @Override
    public FlowBlockSourceControl flow() {
        return ioFlow;
    }

    @Override
    public void appendFlowBlock(Block block) {
        if (ioFlow.controlOutputBlock() != -1)
            user.appendFlowBlock(block);
    }

    @Override
    public void insertBlock(Block block) {
        user.insertBlock(block);
    }

    @Override
    public void closed() {
        user.closed();
    }


    @Override
    public void setDeviceName(String name) {
        user.setDeviceName(name);
    }

    @Override
    public String getDeviceName() {
        return user.getDeviceName();
    }


    @Override
    public void pushAction(BlockAction<BlockUser> action) {
        user.pushAction(action);
    }

    @Override
    public void addAction(String k, BlockAction<BlockUser> v) {
        user.addAction(k, v);
    }

    @Override
    public BlockAction<BlockUser> get(String key) {
        return user.get(key);
    }

    @Override
    public void removeAction(String k) {
        user.removeAction(k);
    }

    @Override
    public void addBlockState(BlockState<BlockUser> action) {
        user.addBlockState(action);
    }

    @Override
    public void delBlockState(BlockState<BlockUser> action) {
        user.delBlockState(action);
    }

}