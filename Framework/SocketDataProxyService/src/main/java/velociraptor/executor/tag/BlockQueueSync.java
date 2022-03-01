package velociraptor.executor.tag;

import velociraptor.block.Block;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static velociraptor.executor.SocketDataService.timeoutMillisNotifyAll;

public abstract class BlockQueueSync implements ISync {

    private   long timeoutMillisNotify=10;
    private final Queue<Block> blocks = new ConcurrentLinkedQueue<>();
    private   long timeoutMillisLimit;
    protected AtomicBoolean useIsClose=new AtomicBoolean(false);

    public void setTimeoutMillisLimit(long timeoutMillisLimit) {
        this.timeoutMillisLimit = timeoutMillisLimit;
    }

    public void setTimeoutMillisNotify(long timeoutMillisNotify) {
        this.timeoutMillisNotify = timeoutMillisNotify;
    }

    protected synchronized void offerToBlocks(Block block) {
        blocks.offer(block);
        if (timeoutMillisNotify < timeoutMillisNotifyAll)
            this.notify();
        else
            this.notifyAll();
    }

    protected synchronized Block toBlock() {
        Block block = null;
        if (!blocks.isEmpty())
            block = blocks.poll();
        return block;
    }

    protected synchronized void sleep() {
        try {
            wait(timeoutMillisNotify);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Block waitReadLine() {
        if (!blocks.isEmpty())
            return blocks.poll();
        int count = timeoutMillisLimit <= 0 ? Integer.MAX_VALUE : (int) (timeoutMillisLimit / timeoutMillisNotify);
        for (int i = 0; i < count && !useIsClose.get() && blocks.isEmpty(); i++)
            sleep();
        return toBlock();
    }

}
