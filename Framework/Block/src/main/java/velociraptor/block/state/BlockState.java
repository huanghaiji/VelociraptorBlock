package velociraptor.block.state;

public interface BlockState<T> {

    void blockState(T u, BlockStateIterator<T> iterator, int state);

}