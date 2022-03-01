package socket.cof;

import velociraptor.so.bot.startup.conf.IdleTimeConfigure;
import velociraptor.so.bot.startup.conf.SocketConfigure;

public class NettyConfigure    implements SocketConfigure {

    public IdleTimeConfigure idleTime;

    public int write_buffer_high_water_mark = -1;
    public int write_buffer_low_water_mark = -1;

}
