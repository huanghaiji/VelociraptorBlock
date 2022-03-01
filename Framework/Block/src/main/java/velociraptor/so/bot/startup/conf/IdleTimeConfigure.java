package velociraptor.so.bot.startup.conf;

/**
 * <pre>
 * IO 活跃检查.
 * write_idle_time < read_idle_time
 * </pre>
 */
public class IdleTimeConfigure implements SocketConfigure {

    /**
     * 单位：秒
     */
    public int read_idle_time = 20;
    /**
     * 单位：秒
     */
    public int write_idle_time = 10;


}