package velociraptor.block.state.list;

import velociraptor.block.state.BlockState;
import velociraptor.block.state.BlockStateIterator;

import java.util.List;

public class BlockStateListMethodIterator<T> implements BlockStateIterator<T> {

    private final List<BlockState<T>> states;
    private int index = 0;
    private int size;

    BlockStateListMethodIterator(List<BlockState<T>> states) {
        this.states = states;
        this.size = states.size();
    }

    BlockStateListMethodIterator(List<BlockState<T>> states, int index) {
        this.states = states;
        this.size = states.size();
        this.index = index;
    }

    @Override
    public synchronized void addBlockState(BlockState<T> action) {
            states.add(action);
    }

    @Override
    public synchronized void delBlockState(BlockState<T> action) {
            int i = states.indexOf(action);
//            index = i <= index ? index : (index--);
            if ( index < i)
                index --;
            size--;
            states.remove(i);
    }

    @Override
    public synchronized boolean hasNext() {
            return index < size && 0 < size;
    }

    @Override
    public synchronized BlockState<T> next() {
            return states.get(index++);
    }

}