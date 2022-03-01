package velociraptor.scheduling.flow;

import velociraptor.block.Block;
import velociraptor.scheduling.interrupt.ErrorInterrupt;

import java.util.concurrent.atomic.AtomicBoolean;

public interface FlowBlockQuantitative extends ErrorInterrupt {

    /**
     * 设置block流的上行数目
     */
    void setMax(int max);

    int getMax();

    /**
     * 获取【确认信】在block流的上行数目达到某个数值时发送
     */
    int getFCLConditions();

    /**
     * 获取当前block流的上行数量
     */
    int getChangeNumber();

    /**
     * block流的上行数量累计
     */
    int incrementChangeNumber();

    /**
     * 重置
     */
    void resetChangeNumber();

    /**
     * 设置【确认信】的block的target。
     */
    void setFCLTarget(String blockTarget);

    /**
     * 获取【确认信】的block的target。
     */
    String FCLTarget();

    /**
     * 【确认信】的回执值，如果为true，表示已回执，则继续上传
     */
    AtomicBoolean FCLCallbackValue();

    /**
     * 设置【确认信】的最小等待时间，单位毫秒
     */
    void setFCLWaitMill(int mill);

    /**
     * 获取【确认信】的最小等待时间，单位毫秒
     */
    int FCLWaitMill();

    /**
     * block身份标记
     */
    void setBlockIdentity(Block identity);

    Block blockIdentity();

}