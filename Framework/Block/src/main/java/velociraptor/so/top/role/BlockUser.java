package velociraptor.so.top.role;

import velociraptor.block.*;
import velociraptor.block.state.BlockState;
import velociraptor.block.state.list.BlockStateArrayList;
import velociraptor.block.state.list.SSTate;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.scheduling.flow.FlowBlockQuantitativePackage;
import velociraptor.scheduling.flow.FlowBlockSourceControl;
import velociraptor.scheduling.interrupt.ErrorInterruptValue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static velociraptor.scheduling.flow.ConfirmationLatterValue.OS;

public abstract class BlockUser extends BlockStateArrayList<BlockUser>
        implements
        SSTate, BlockUsr,
        FlowBlockQuantitativePackage,
        ErrorInterruptValue,
        BlockContext2 {

    private BlockUserActionMap actions;
    private FlowBlockQuantitative quantitative;
    /**
     * only num
     */
    private final AtomicBoolean isClose = new AtomicBoolean(false);
    /**
     * only num
     */
    private final AtomicBoolean isOpen = new AtomicBoolean(false);
    private BlockContext context;
    private final AtomicBoolean errorInterruptValue = new AtomicBoolean(false);

    private String deviceName = "NONE";
    private final FlowBlockSourceControl flow = new FlowBlockSourceControl() {
        @Override
        public FlowBlockQuantitative quantitative() {
            return quantitative;
        }

        @Override
        public Block instanceBlock() {
            return context.instanceBlock();
        }

        @Override
        public void analysis(Void ctx, Block block) {
            insertBlock(block);
        }
    };


    @Override
    public void setBlockContext(BlockContext context) {
        this.context = context;
    }

    @Override
    public BlockContext context() {
        return context;
    }

    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public void pushAction(BlockAction<BlockUser> action) {
        this.actions.pushAction(action);
    }

    @Override
    public void addAction(String k, BlockAction<BlockUser> v) {
        this.actions.addAction(k, v);
    }

    @Override
    public BlockAction<BlockUser> get(String key) {
        return this.actions.get(key);
    }

    @Override
    public void removeAction(String k) {
        actions.removeAction(k);
    }

    @Override
    public void addBlockState(BlockState<BlockUser> action) {
        super.addBlockState(action);
        if (isOpen())
            targetBlockStateChange(this, OPEN, action);
        else if (isClose())
            targetBlockStateChange(this, CLOSE, action);
    }

    @Override
    public AtomicBoolean getMainErrorInterruptValue() {
        return errorInterruptValue;
    }

    @Override
    public void setFlowBlockParameter(FlowBlockQuantitative parameter) {
        quantitative = parameter;
        quantitative.addErrorInterrupt(this);
    }


    @Override
    public FlowBlockQuantitative quantitative() {
        return quantitative;
    }

    @Override
    public FlowBlockSourceControl flow() {
        return flow;
    }

    public void setActions(BlockUserActionMap map) {
        if (map != null)
            this.actions = map;
    }


    protected abstract void write(Object bytes);

    protected abstract void close0();

    public void appendFlowBlock(Block block) {
        if (block == null) return;
        if (flow.controlOutputBlock() != -1)
            insertBlock(block);
    }

    @Override
    public void insertBlock(Block block) {
        this.write(context.write().full(block));
    }

    /**
     * <pre>
     *     server:
     *              1,quantitative.confirmationLatterTarget() ,
     *              2,block.data .equals(ConfirmationLatterTargetValue.OS)
     * </pre>
     */
    public void offerBlock(byte[] bytes) throws IOException {
        BlockFullReader reader = context.reader();
        reader.push(bytes);
        for (Block block; (block = reader.readBlock()) != null; ) {
            String target = block.getTarget();
            if (target.equals(quantitative.FCLTarget()) && block.getDataUTF8().equals(OS)) {
                flow.FCLCallback();
                System.out.println("OS解除流速限制");
                continue;
            }
            BlockAction<BlockUser> action = get(target);
            if (action != null) {
                action.analysis(this, block);
            } else {
                //1。不存在该处理事件时
                //2。处理该事件的配置还没完成。
                if (Debug.Output.__ != null)
                    Debug.Output.__.debugOfferBlock(this, block);
            }
        }
    }

    public void opened() {
        System.out.println(this + " >> open " + isOpen());
        if (!isOpen()) {
            isOpen.set(true);
            blockStateChange(this, SSTate.OPEN);
        }
    }

    public void closed() {
        if (!isClose.get()) {
            try {
                System.out.println(this + " >> close," + getDeviceName());
                isClose.set(true);
                errorInterruptValue.set(true);
                close0();
            } finally {
                blockStateChange(this, SSTate.CLOSE);
            }
        }
    }


    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public boolean isClose() {
        return isClose.get();
    }


}