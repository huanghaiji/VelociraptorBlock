package velociraptor.block;

public interface BlockAction<T> {

    void analysis(T ctx, Block block);

}
