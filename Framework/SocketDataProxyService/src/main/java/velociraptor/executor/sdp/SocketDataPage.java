package velociraptor.executor.sdp;

public interface SocketDataPage {

    /**
     * 准备接收数据等事件
     */
    void onStart();

    /**
     * 结束接收数据等事件
     */
    void onStop();

    SocketDataServer service();
}