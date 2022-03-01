package velociraptor.block;

public interface BlockContext {

    Block instanceBlock();

    BlockFullWrite write();

    BlockFullReader reader();

}
