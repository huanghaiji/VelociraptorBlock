package velociraptor.scheduling.flow;

import velociraptor.block.Block;
import velociraptor.block.BlockAction;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 *     block源头流量控制
 *     FCL = flow confirmation latter
 * </pre>
 */
public interface FlowBlockSourceControl extends FlowBlockQuantitativePackage, BlockAction<Void> /*block output*/ {

    /**
     * 确认信block
     */
    default Block FCLBlock() {
        FlowBlockQuantitative quantitative = quantitative();
        Block block = instanceBlock();
        Block identity = quantitative.blockIdentity();
        identity.copyId(block);
        block.setTarget(quantitative.FCLTarget());
        block.setDataUTF8(ConfirmationLatterValue.QRX);
        return block;
    }

    Block instanceBlock();


    int FCL_MAX = Integer.MAX_VALUE;

    /**
     * 控制block流的输出
     * <pre>
     *      1,如果发生error错误，则返回-1.
     *      2,step1没发生，则返回block累计数.
     *      3,不应该直接在read的事件中阻塞线程.
     *      4, 未设置block数据超过长度而分割N份Block发送。
     *      5,控制当前线程的流输出到网络的速度，发送或者等待结果再发送
     * </pre>
     */
    default int controlOutputBlock() {
        FlowBlockQuantitative quantitative = this.quantitative();
        int number = quantitative.getChangeNumber();
        int max = quantitative.getMax();
        int mid = quantitative.getFCLConditions();
        if (max != FCL_MAX) {
            System.out.println("block num:" + number);
            if (quantitative.errorInterrupt())
                return -1;
            //发送 “ 确认信 ",代码编号 ：QRX
            if (number == mid) {
                quantitative.FCLCallbackValue().set(false);
                analysis(null, FCLBlock());
            }
            //block 统计量超过 阈值，等待 ” 确认信 " 回执
            if (max < number) {
                int mill = quantitative.FCLWaitMill();
                AtomicBoolean clv = quantitative.FCLCallbackValue();
                while (!clv.get()) {
                    if (quantitative.errorInterrupt()) {
                        return -1;
                    }
                    synchronized (this) {
                        try {
                            wait(mill);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                quantitative.resetChangeNumber();
            }
            //if block output  and increment change number.
            number = quantitative.incrementChangeNumber();
        }
        return number;
    }

    /**
     * 在分时分段模式中，不能够采用任何wait等方式迫使线程处于挂起状态。
     */
    default int controlOutputBlock2(){
        FlowBlockQuantitative quantitative = this.quantitative();
        int number = quantitative.getChangeNumber();
        int max = quantitative.getMax();
        int mid = quantitative.getFCLConditions();
        if (max != FCL_MAX) {
            System.out.println("block num:" + number);
            if (quantitative.errorInterrupt())
                return -1;
            //发送 “ 确认信 ",代码编号 ：QRX
            if (number == mid) {
                quantitative.FCLCallbackValue().set(false);
                analysis(null, FCLBlock());
            }
            //block 统计量超过 阈值，等待 ” 确认信 " 回执
            if (max < number) {
                if (quantitative.FCLCallbackValue().get())
                    quantitative.resetChangeNumber();
            }
            //if block output  and increment change number.
            number = quantitative.incrementChangeNumber();
        }
        return number;
    }

    /**
     * 确认信回执
     */
    default void FCLCallback() {
        quantitative().FCLCallbackValue().set(true);
    }

}