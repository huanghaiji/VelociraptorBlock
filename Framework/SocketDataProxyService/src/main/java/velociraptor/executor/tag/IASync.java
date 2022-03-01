package velociraptor.executor.tag;

public interface IASync extends ISG{

    /**
     * 接收来自该location的信息。不同于广播，接收的block的id与Service是一致的。
     *<span style="color:red">注意，其他端发数据之前，就应该完成对该location的监视，不然就会出现遗漏而错过。</span>
     * 最好在双方约定的情况下进行。
     */
    IASync observeLocation(String location);

    /**
     *发送数据
     */
    IASync onSending(byte[] bytes,String location);

    IASync onSending(String data,String charset,String location);
}
