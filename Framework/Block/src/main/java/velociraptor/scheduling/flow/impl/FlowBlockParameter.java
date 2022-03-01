package velociraptor.scheduling.flow.impl;

import velociraptor.block.Block;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.scheduling.interrupt.ErrorInterrupt;
import velociraptor.scheduling.interrupt.ErrorInterruptArrayList;
import velociraptor.scheduling.interrupt.ErrorInterruptValue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowBlockParameter implements FlowBlockQuantitative {

    private final AtomicInteger max = new AtomicInteger(Integer.MAX_VALUE);
    private String confirmationLatterTarget;
    private final AtomicInteger changeNumber = new AtomicInteger(0);
    private Block blockIdentity;
    private final AtomicBoolean confirmationLatterCallbackValue = new AtomicBoolean(true);
    private int confirmationLatterWaitMill = 50;
    private final ErrorInterrupt error = new ErrorInterruptArrayList();


    public void setMax(int max) {
        if (max > 0)
            this.max.set(max);
    }

    @Override
    public int getMax() {
        return max.get();
    }

    public int getFCLConditions() {
        return max.get() / 2;
    }

    @Override
    public void setFCLTarget(String blockTarget) {
        if ( confirmationLatterTarget==null || confirmationLatterTarget.equals(""))
            this.confirmationLatterTarget = blockTarget;
        else
            System.out.println("仅一次，不可再修改,[confirmationLatterTarget]");
    }

    @Override
    public String FCLTarget() {
        return confirmationLatterTarget;
    }

    @Override
    public AtomicBoolean FCLCallbackValue() {
        return this.confirmationLatterCallbackValue;
    }

    @Override
    public void setFCLWaitMill(int mill) {
        this.confirmationLatterWaitMill = mill;
    }

    @Override
    public int FCLWaitMill() {
        return this.confirmationLatterWaitMill;
    }

    @Override
    public void setBlockIdentity(Block identity) {
        if (this.blockIdentity == null)
            this.blockIdentity = identity;
        else
            System.out.println("仅一次，不可再修改，[blockIdentity]");
    }

    @Override
    public Block blockIdentity() {
        return this.blockIdentity;
    }


    public int getChangeNumber() {
        return changeNumber.get();
    }

    public int incrementChangeNumber() {
        return changeNumber.incrementAndGet();
    }

    public void resetChangeNumber() {
        changeNumber.set(0);
    }


    @Override
    public void addErrorInterrupt(ErrorInterruptValue value) {
        this.error.addErrorInterrupt(value);
    }

    @Override
    public boolean errorInterrupt() {
        return this.error.errorInterrupt();
    }

}