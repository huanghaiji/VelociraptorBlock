import org.junit.Test;
import socket.startup.NettyBlockUserServer;
import velociraptor.block.Block;
import velociraptor.block.BlockAction;
import velociraptor.block.state.list.SSTate;
import velociraptor.block.target.BlockBusiness;
import velociraptor.executor.*;
import velociraptor.executor.sdp.broadcast.SocketDataPageBroadcast;
import velociraptor.protocol.BlockData;
import velociraptor.so.bot.startup.SocketClientWorker;
import velociraptor.so.bot.startup.SocketClientWorkerProxy;
import velociraptor.so.bot.startup.SocketServerWorker;
import velociraptor.so.bot.startup.conf.Address;
import velociraptor.so.bot.startup.conf.IdleTimeConfigure;
import velociraptor.so.top.role.BlockUser;
import velociraptor.so.top.role.BlockUserActionConcurrentHashMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class SocketDataProxyServiceTest {
    private static final DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
    private final Map<Integer, BlockUser> users = new ConcurrentHashMap<>();

    private void startTimer() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            if (!users.isEmpty())
                synchronized (users) {
                    for (Map.Entry<Integer, BlockUser> entry : users.entrySet()) {
                        System.out.println("TIme post");
                        Block block = new BlockData(System.nanoTime(), "Servlet", "timer");
                        block.setData(df.format(System.currentTimeMillis()).getBytes());
                        entry.getValue().insertBlock(block);
                    }
                }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @BlockBusiness("login")
    class LoginServletAction implements BlockAction<BlockUser> {

        @Override
        public void analysis(BlockUser ctx, Block block) {
            block.setData((block.getDataUTF8() + " >>>login ok").getBytes());
            ctx.insertBlock(block);
            synchronized (users) {
                if (!users.containsKey(ctx.hashCode())) {
                    ctx.addBlockState((u, iterator, state) -> {if (state == SSTate.CLOSE)users.remove( u.hashCode());});
                    users.put(ctx.hashCode(), ctx);
                }
            }
        }
    }

    @BlockBusiness("alert")
    static class AlertServletAction implements BlockAction<BlockUser>{
        @Override
        public void analysis(BlockUser ctx, Block block) {
            block.setDataUTF8("通知一\r\n通知二\r\n通知三");
            ctx.insertBlock(block);
        }
    }

    @BlockBusiness("topics")
    static class TopicsServletAction implements BlockAction<BlockUser>{

        @Override
        public void analysis(BlockUser ctx, Block block) {
            block.setDataUTF8(
                    """ 
                            1，迅猛龙的架构中FRA分别指那三层
                            """
            );
            ctx.insertBlock(block);
        }
    }

    //服务器
    @Test
    public void startService() throws Exception {
        SocketServerWorker server = new NettyBlockUserServer(9394, new IdleTimeConfigure());
        server.pushBlockAction(new LoginServletAction());
        server.pushBlockAction(new AlertServletAction());
        server.pushBlockAction(new TopicsServletAction());
        startTimer();
        server.startup();
    }

    @Test
    public void testSync() throws Exception {
        //开启Nio Socket 各项服务
        SocketDataService service = new SocketDataService();
        client.startup();
        //创建设备账户
        BlockUser user = client.createUser(new Address("127.0.0.1", 9394), null, null);
        user.setActions(new BlockUserActionConcurrentHashMap());

        //创建广播
        SocketDataPageBroadcast broadcast = new SocketDataBroadcast(service, user, (user1, block, location, broadcast1) -> System.out.println("来自服务器的广播:>>>" + block.getDataUTF8() + " ," + location));
        broadcast.onStart("timer");

        //持续发送，并且持续监听数据回应
        SocketDataPageASync async = new SocketDataPageASync(service, user, (bytes, proxy) -> System.out.println("异步接收---->" + new String(bytes)));
        async.onStart();
        for (int i = 0; i < 5; i++) {
            async.onSending("User2{name:'admin',id:123}".getBytes(), "login");
        }

        //持续 双同步
        SocketDataPageSync sync = new SocketDataPageSync(user, service);
        sync.onStart();
        for (int i = 0; i < 5; i++) {
            Block block = sync.onSending("User{name:'admin',id:123}".getBytes(), "login").waitReadLine();
            System.out.println(i + "----->" + block.getDataUTF8());
        }
        sync.onStop();
        async.onStop();

        synchronized (this) {
            wait(30 * 1000);
        }
        broadcast.onStop();
    }


    SocketDataService server = new SocketDataService();
    SocketClientWorker client = new SocketClientWorkerProxy();


    @Test //NIO Socket
    public void testAsync() throws Exception{
        client.startup();

        BlockUser user =  client.createUser( new Address("127.0.0.1",9394),null,null);
        user.setActions(new BlockUserActionConcurrentHashMap());
        user.setDeviceName("pc.appdoor.mac.0001");

        Block block = new BlockData(1, user.getDeviceName(),"login");
        block.setDataUTF8("{admin, 123456}");
        user.appendFlowBlock( block);
        user.addAction("login", (ctx, block12) -> {
            ctx.removeAction("login");
            System.out.println("ok,登录完成:" + block12.getDataUTF8());
            Block block1 = new BlockData(2, user.getDeviceName(), "alert");
            ctx.appendFlowBlock(block1);
            ctx.addAction("alert", (ctx1, block2) -> {
                System.out.println("ok,通知加载完成:" + block2.getDataUTF8());
                ctx.removeAction("alert");
                block2 = new BlockData(3,user.getDeviceName(),"topics");
                ctx.appendFlowBlock(block2);
                ctx.addAction("topics",(ctx2,block3)->{
                    System.out.println("ok,议题加载完成:"+ block3.getDataUTF8());
                    ctx2.removeAction("topics");
                });
            });
        });
        synchronized (this){
            wait(4*1000);
        }
        user.closed();
    }

    @Test //Sync SocketDataPage Server
    public void testSync2() throws Exception{
        client.startup();
        BlockUser user= client.createUser( new Address("127.0.0.1",9394),null,null);
        user.setActions(new BlockUserActionConcurrentHashMap());
        user.setDeviceName("pc.appdoor.mac.0002");

        SocketDataPageSync sync = new SocketDataPageSync(user,server);
        sync.onStart();

        System.out.println("ok,登录完成:"+ sync.onSending("{admin,123456}".getBytes(),"login").waitReadLine().getDataUTF8());
        System.out.println("ok,通知加载完成:" + sync.onSending(null,"alert").waitReadLine().getDataUTF8());
        System.out.println("ok,议题加载完成:"+sync.onSending(null,"topics").waitReadLine().getDataUTF8() );
        sync.onStop();
        user.closed();
    }


}
