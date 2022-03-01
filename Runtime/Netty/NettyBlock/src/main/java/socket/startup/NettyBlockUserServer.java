package socket.startup;

import velociraptor.block.BlockAction;
import velociraptor.block.state.BlockState;
import velociraptor.block.state.list.BlockStateArrayList;
import velociraptor.scheduling.flow.impl.FlowBlockParameter;
import velociraptor.so.bot.startup.conf.IdleTimeConfigure;
import velociraptor.so.bot.startup.SocketServerWorker;
import velociraptor.so.top.role.BlockUser;
import velociraptor.so.top.role.BlockUserActionMap;
import velociraptor.so.top.role.BlockUserActionConcurrentHashMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import socket.NettyBlockUser;
import socket.folding.keepalive.NettyKeepAlive;

public class NettyBlockUserServer extends
        ChannelInitializer<SocketChannel> implements
        SocketServerWorker {

    public final int port;
    public IdleTimeConfigure idleTime;

    private final BlockUserActionMap blockUserActionMap = new BlockUserActionConcurrentHashMap();
    private final BlockStateArrayList<BlockUser> blockUserStateArray = new BlockStateArrayList<>();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private CreateBlockUserCallback cu;

    public NettyBlockUserServer(int port, IdleTimeConfigure idleTime) {
        this.port = port;
        this.idleTime = idleTime;
    }

    public void pushBlockAction(BlockAction<BlockUser> action) {
        blockUserActionMap.pushAction(action);
    }

    @Override
    public void addBlockStateAction(BlockState<BlockUser> state) {
        blockUserStateArray.addBlockState(state);
    }

    @Override
    public int port() {
        return port;
    }

    public void startup() throws Exception {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
            ServerBootstrap strap = new ServerBootstrap();
            strap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = strap.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        NettyBlockUser user = new NettyBlockUser();

        ch.pipeline().addLast(new NettyKeepAlive(idleTime, user));
        user.setFlowBlockParameter(new FlowBlockParameter());
        user.setActions(blockUserActionMap);
        user.addBlockState(blockUserStateArray);
        user.setSocket(ch);
        if (cu!=null)
            cu.callback(user);
    }

    @Override
    public void setCreateBlockUserCallback(CreateBlockUserCallback callback) {
        this.cu =callback;
    }

}