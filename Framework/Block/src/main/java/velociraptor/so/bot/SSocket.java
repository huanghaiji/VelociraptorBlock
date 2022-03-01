package velociraptor.so.bot;

/**
 * 抽象的socket方面的定义。
 */
public interface SSocket {

    void startup() throws Exception;

    void close();
}
