package velociraptor.scheduling.interrupt;

/**
 * 错误中断
 */
public interface ErrorInterrupt {


    void addErrorInterrupt(ErrorInterruptValue value);

    boolean errorInterrupt();


}