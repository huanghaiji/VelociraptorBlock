package velociraptor.block.state;

public interface BlockStateArray<T> {

    /**
     * 同一个object时， action 中需要 添加 BlockStateAction，则使用BlockStateArrayMethodIterator 添加
     */
    void addBlockState(BlockState<T> action);

    /**
     * 同一个object时， action 中需要 刪除 BlockStateAction，则使用BlockStateArrayMethodIterator 删除
     */
    void delBlockState(BlockState<T> action);


}
