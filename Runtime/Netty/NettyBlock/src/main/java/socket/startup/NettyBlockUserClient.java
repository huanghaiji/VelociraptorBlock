package socket.startup;

import velociraptor.scheduling.flow.impl.FlowBlockParameter;
import velociraptor.so.bot.startup.conf.Address;
import velociraptor.scheduling.flow.FlowBlockQuantitative;
import velociraptor.so.bot.startup.SocketClientWorker;
import velociraptor.so.bot.startup.conf.IdleTimeConfigure;
import velociraptor.so.bot.startup.conf.SocketConfigure;
import velociraptor.so.bot.startup.conf.SocketConfigureAttributeReference;
import velociraptor.so.top.role.BlockUser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import socket.NettyBlockUser;
import socket.cof.NettyConfigure;
import socket.folding.keepalive.NettyKeepAlive;

import java.util.concurrent.TimeUnit;

public class NettyBlockUserClient extends ChannelInitializer<SocketChannel> implements SocketClientWorker {

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    @Override
    protected synchronized void initChannel(SocketChannel channel) {
        System.out.println("init channel....");
//        ChannelId id = channel.id();
//        if (ids.contains(id)) {
//            ids.remove(id);
//        } else {
//            ids.add(channel.id());
//        }
    }


    @Override
    public void startup() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(this);

        this.bootstrap = bootstrap;
        this.workerGroup = workerGroup;

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        workerGroup.shutdownGracefully();
    }


    @Override
    public synchronized BlockUser createUser(Address address, SocketConfigure setting, FlowBlockQuantitative quantitative) {

        if (setting==null)
            setting = new NettyConfigure();
        if (quantitative==null)
            quantitative = new FlowBlockParameter();

        NettyBlockUser user = new NettyBlockUser();
        user.setFlowBlockParameter(quantitative);


        NettyConfigure configure = setting instanceof NettyConfigure? (NettyConfigure) setting : SocketConfigureAttributeReference.onBuild(new NettyConfigure(),setting);

        ChannelFuture f = bootstrap.connect(address.name, address.port);
        Channel channel = f.channel();


        ChannelConfig config = channel.config();
        System.out.println("low:" + config.getWriteBufferLowWaterMark() + " high:" + config.getWriteBufferHighWaterMark());
        System.out.println("connection-timeout:" + config.getConnectTimeoutMillis()+"ms");

        if (  configure.write_buffer_high_water_mark != -1)
            config.setWriteBufferLowWaterMark(configure.write_buffer_low_water_mark);
        if (  configure.write_buffer_high_water_mark != -1)
            config.setWriteBufferHighWaterMark(configure.write_buffer_high_water_mark);
        if ( configure.idleTime==null)
            configure.idleTime=new IdleTimeConfigure();

        System.out.println("threadWorker user");

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new NettyKeepAlive(configure.idleTime, user));
        user.setSocket(channel);
        channel.closeFuture().addListener(future -> {
            System.out.println("exit---->");
            workerGroup.schedule(user::closed, 1, TimeUnit.SECONDS);
        });

        //如果initChannel 优先实例了，则可能导致反应堆没open反应。
//        ChannelId id = channel.id();
//        if (ids.contains(id)) {
//            ids.remove(id);
//            user.open();
//        } else {
//            ids.add(id);
//        }

        System.out.println("threadWorker end");
        return user;
    }

}