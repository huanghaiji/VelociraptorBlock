package velociraptor.scheduling.flow;

import velociraptor.block.Block;

public interface FlowBlockTargetControl {


    /**
     * 发回确认信block。
     */
    default Block responseConfirmationLatter(Block confirmationLatterBlock) {
        return confirmationLatterBlock;
    }


}