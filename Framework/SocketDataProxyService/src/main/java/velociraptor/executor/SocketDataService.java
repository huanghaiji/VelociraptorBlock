package velociraptor.executor;

import velociraptor.block.Block;
import velociraptor.executor.sdp.SocketDataServer;
import velociraptor.executor.tag.ISG;
import velociraptor.so.top.role.BlockUser;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class SocketDataService implements SocketDataServer {

    /**
     * 当timeoutMillisNotify设置太长,超过timeoutMillisNotifyAll，则notifyAll，已避免不确定的线程唤醒问题
     */
    public static final long timeoutMillisNotifyAll = 2000;
    static final AtomicLong id = new AtomicLong(0);

    private final ExecutorService executor = Executors.newScheduledThreadPool(2);
    private final Map<Long, ISG> pages = new ConcurrentHashMap<>();
    private final Queue<Async> asyncQueue = new ConcurrentLinkedQueue<>();


    private static final class Async {
        final ISG sdp;
        final Block block;

        public Async(ISG sdp, Block block) {
            this.sdp = sdp;
            this.block = block;
        }

        public void onRev() {
            sdp.onRev(block);
        }
    }

    SocketDataPageAction checkBlockUserInterfaceLocation(BlockUser user, String location){
        SocketDataPageAction listening = (SocketDataPageAction) user.get(location);
        if (listening==null){
            synchronized (this){
                if (( listening = (SocketDataPageAction) user.get(location))==null)
                    user.addAction(location, listening=new SocketDataPageAction(this,location));
            }
        }
        return listening;
    }

    void commitAsync(ISG sdp, Block block) {
        asyncQueue.offer(new Async(sdp, block));
        executor.execute(() -> {
            Async async = asyncQueue.poll();
            if (async != null) async.onRev();
        });
    }

    void pagesAdd(long key, ISG sdp) {
        pages.put(key, sdp);
    }

    ISG pagesGet(long key) {
        return pages.get(key);
    }

    void pagesRemove(long key) {
        pages.remove(key);
    }

}