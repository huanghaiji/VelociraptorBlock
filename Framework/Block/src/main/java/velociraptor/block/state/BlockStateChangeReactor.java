package velociraptor.block.state;

public interface BlockStateChangeReactor<T> {

    void blockStateChange(T u, int state);

    void targetBlockStateChange(T u, int state, BlockState<T> action);

}
