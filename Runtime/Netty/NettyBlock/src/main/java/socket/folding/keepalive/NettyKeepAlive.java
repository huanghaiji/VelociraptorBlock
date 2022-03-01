package socket.folding.keepalive;

import velociraptor.so.bot.SSKeepAliveBlock;
import velociraptor.so.bot.startup.conf.IdleTimeConfigure;
import velociraptor.so.top.role.BlockUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyKeepAlive extends IdleStateHandler {

    private final BlockUser bu;

    public NettyKeepAlive(IdleTimeConfigure idle, BlockUser bu) {
        super(idle.read_idle_time, idle.write_idle_time, 0, TimeUnit.SECONDS);
        this.bu = bu;
    }

    /**
     * client ping-pong server,
     * server ping-pong client.
     */
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        switch (evt.state()) {
            case WRITER_IDLE ->
                    bu.insertBlock(new SSKeepAliveBlock() {
                    }.instanceKeepAliveBlock(
                            bu.context().instanceBlock(),
                            bu.getDeviceName()));
            case READER_IDLE -> bu.closed();
            default -> System.out.println("other: " + evt.state());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("active");
        bu.opened();
    }

}