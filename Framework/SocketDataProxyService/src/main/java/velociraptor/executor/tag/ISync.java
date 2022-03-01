package velociraptor.executor.tag;

import velociraptor.block.Block;

public interface ISync extends ISG{

    /**
     * 接收来自该location的信息。不同于广播，接收的block的id与Service是一致的。
     *<span style="color:red">注意，其他端发数据之前，就应该完成对该location的监视，不然就会出现遗漏而错过。</span>
     * 最好在双方约定的情况下进行。
     */
    ISync observeLocation(String location);
    /**
     *发送数据
     */
    ISync onSending(byte[] bytes, String location);

    ISync onSending(String data,String charset,String location);
    /**
     * 阻塞接收数据
     */
    Block waitReadLine();

}
