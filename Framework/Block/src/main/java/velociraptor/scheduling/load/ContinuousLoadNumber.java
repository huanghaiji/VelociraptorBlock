package velociraptor.scheduling.load;

public interface ContinuousLoadNumber {

    /**
     * 增加一个负荷量
     */
    void incrementNum();

    /**
     * 减少一个负荷量
     */
    void decrementNum();

    /**
     * 减少多个负荷量
     */
    default void decrementNum(int num) {

    }

    int loadNumber();

}
