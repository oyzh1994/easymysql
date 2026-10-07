package cn.oyzh.easymysql.exception;

/**
 * 只读操作异常
 *
 * @author oyzh
 * @since 2023/12/09
 */
public class ReadonlyOperationException extends DBException {

    /**
     * 构造方法
     */
    public ReadonlyOperationException() {
        this("只读模式不支持此操作");
    }

    /**
     * 构造方法
     *
     * @param msg 异常信息
     */
    public ReadonlyOperationException(String msg) {
        super(msg);
    }
}
