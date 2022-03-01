package socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import velociraptor.block.state.list.SSTate;
import velociraptor.protocol.BlockDataContext;
import velociraptor.so.top.role.BlockUser;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NettyBlockUser extends BlockUser {


    private Channel socket;
    private QueueBytes queueBytes = new QueueBytes();

    public NettyBlockUser() {
        this.setBlockContext(new BlockDataContext());
    }

    public void setSocket(Channel context) {
        this.socket = context;
        ChannelPipeline pipeline = context.pipeline();
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf buf = (ByteBuf) msg;
                try {
                    while (buf.isReadable()) {
                        byte[] bytes = new byte[buf.readableBytes()];
                        buf.readBytes(bytes);
                        offerBlock(bytes);
                    }
                } finally {
                    ReferenceCountUtil.release(msg);
                }
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                System.err.println("channel inactive");
                closed();
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                if (cause instanceof IOException) {
                    //state:
                    //  1, io error
                    //  2, close
                    //      从某个角度来说，有必要io-error 同时 close吗？？而不是直接close吗？？（流本身就直接断了）
                    blockStateChange(NettyBlockUser.this, SSTate.IO_ERROR);
                    System.err.println("exception caught");
                    closed();
                }
                cause.printStackTrace();
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                System.out.println("----------------------------ctx:open--------");
                opened();
                queueBytes.loopBytesWrite();
            }

        });
        //pipeline.close().addListener(future -> close());
    }


    public class QueueBytes {
        private final Queue<byte[]> blocks = new ConcurrentLinkedQueue<>();
        private boolean isLoopBytesWrite;

        public synchronized boolean offerBytes(byte[] bytes) {
            if (!queueBytes.isLoopBytesWrite) {
                blocks.offer(bytes);
                return true;
            } else {
                queueBytes = null;
                return false;
            }
        }

        public synchronized void loopBytesWrite() {
            isLoopBytesWrite = true;
            if(!blocks.isEmpty())
            for (byte[] bytes; (bytes = blocks.poll()) != null; )
                write0(bytes);
            queueBytes = null;
        }

    }

    @Override
    protected void write(Object o) {
        byte[] bytes = (byte[]) o;
        if (queueBytes != null && queueBytes.offerBytes(bytes)) return;
        if (socket != null) write0(bytes);
    }

    private void write0(byte[] bytes) {
        ByteBuf buf = socket.alloc().buffer(bytes.length).writeBytes(bytes);
        socket.writeAndFlush(buf);
    }

    @Override
    protected void close0() {
        if (socket != null) {
            socket.close().addListener(ChannelFutureListener.CLOSE);
        }
        socket = null;
    }


}