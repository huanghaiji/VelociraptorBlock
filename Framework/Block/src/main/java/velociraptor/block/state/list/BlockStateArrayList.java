package velociraptor.block.state.list;

import velociraptor.block.state.BlockState;
import velociraptor.block.state.BlockStateArray;
import velociraptor.block.state.BlockStateIterator;
import velociraptor.block.state.BlockStateChangeReactor;

import java.util.ArrayList;
import java.util.List;

public class BlockStateArrayList<T> implements BlockStateArray<T>, BlockStateChangeReactor<T> {

    private final List<BlockState<T>> states = new ArrayList<>();

    public synchronized void addBlockState(BlockState<T> action) {
        if (action == null) return;
        states.add(action);
    }

    public synchronized void addBlockState(BlockStateArrayList<T> bsa) {
        if (bsa == null) return;
        for (BlockState<T> action : bsa.states)
            addBlockState(action);
    }

    public synchronized void delBlockState(BlockState<T> action) {
        states.remove(action);
    }

    public synchronized void blockStateChange(T u, int state) {
        if (!states.isEmpty()) {
            BlockStateIterator<T> iterator = new BlockStateListMethodIterator<>(this.states);
            while (iterator.hasNext()) {
                try {
                    do {
                        BlockState<T> action = iterator.next();
                        action.blockState(u, iterator, state);
                    } while (iterator.hasNext());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void targetBlockStateChange(T u, int state, BlockState<T> action) {
        BlockStateIterator<T> iterator = new BlockStateListMethodIterator<>(states, states.indexOf(action) + 1);
        action.blockState(u, iterator, state);
    }

}