package velociraptor.so.bot.startup;

import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.so.bot.SSocket;
import velociraptor.so.bot.startup.conf.Address;
import velociraptor.so.bot.startup.conf.SocketConfigure;
import velociraptor.so.top.role.BlockUser;

/**
 * Client
 */
public interface SocketClientWorker extends SSocket {

    /**
     * host and port  or other configure.
     */
    BlockUser createUser(Address address, SocketConfigure configure, FlowBlockQuantitative quantitative);


}