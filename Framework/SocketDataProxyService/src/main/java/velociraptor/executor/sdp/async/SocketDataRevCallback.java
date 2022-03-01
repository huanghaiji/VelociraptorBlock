package velociraptor.executor.sdp.async;

import velociraptor.executor.tag.IASync;

/**
 * 当为异步时，则在此接口定义实现类
 */
public interface SocketDataRevCallback {

    void onRev(byte[] bytes, IASync proxy);

}