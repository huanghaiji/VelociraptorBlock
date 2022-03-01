package velociraptor.so.top.role;

import velociraptor.block.Block;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.scheduling.flow.FlowBlockSourceControl;

import java.util.concurrent.ScheduledExecutorService;

public interface BlockUsr extends BlockUserActionMap, DeviceName {

    FlowBlockQuantitative quantitative();

    FlowBlockSourceControl flow();

    default void opened() {}

    void closed();

    /**
     * 不能在read线程中使用，造成线程阻塞。下列代码错误
     * <pre>
     * read(...){
     *      u.appendFlowBlock(....)
     * }
     * </pre>
     * 流控制的等待过程是阻塞的。
     */
    void appendFlowBlock(Block block);

    /**
     * 以直接的方式发送block，跳过Block流控制。非阻塞的。
     */
    void insertBlock(Block block);

    interface Debug{
       abstract class Output{

            /**
             * 对user的offerBlock进行debug
             */
            public abstract void debugOfferBlock(BlockUser user,Block block);

            public static Output __;

        }
    }

}