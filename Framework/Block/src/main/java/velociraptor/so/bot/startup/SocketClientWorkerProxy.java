package velociraptor.so.bot.startup;

import velociraptor.ClassRoute.ClassPathSearching;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.so.SocketPointStub;
import velociraptor.so.bot.startup.conf.Address;
import velociraptor.so.bot.startup.conf.SocketConfigure;
import velociraptor.so.top.role.BlockUser;

public class SocketClientWorkerProxy implements SocketClientWorker {

    public  SocketClientWorker instance() {
        ClassPathSearching searching = new ClassPathSearching();
        searching.addClassURL(SocketPointStub.class);
        SocketClientWorker client = null;
        for (Class<?> cls : searching) {
            if (cls!=SocketClientWorker.class &&SocketClientWorker.class.isAssignableFrom(cls)) {
                client = (SocketClientWorker) searching.instanceClass(cls);
                break;
            }
        }
        return client;
    }

    private final SocketClientWorker client;
    public SocketClientWorkerProxy(){
        client = instance();
    }

    @Override
    public void startup() throws Exception {
        client.startup();
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public BlockUser createUser(Address address, SocketConfigure configure, FlowBlockQuantitative quantitative) {
        return client.createUser( address, configure, quantitative);
    }

}
