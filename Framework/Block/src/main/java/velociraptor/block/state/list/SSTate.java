package velociraptor.block.state.list;

public interface SSTate {

    int OPEN = 1;

    int CLOSE = -1;

    /**
     * 如果相关关联的另一条连接断了，则服务器向另一方连接发送IO错误.
     */
    int IO_ERROR = -2;

    boolean isOpen();

    boolean isClose();

}
