package velociraptor.scheduling.flow;

/**
 *
 *
 * 确认信要保证接收者能够收到该消息，否则则视为 不可确认。
 */
public class ConfirmationLatterValue {

    /**
     * 该确认信是由server返回的
     */
    public static final String OS = "OS";

    /**
     * client发送的确认信的代码， 与NOT 相反，如果接收方直接返回此代码，则证明确认信可靠
     */
     static final String QRX = "QRX";

    /**
     * 确认信不可靠，那么应该返回该类型，以此表达该确认信无法找到响应者
     */
    public static final String NOT = "NOT";

}