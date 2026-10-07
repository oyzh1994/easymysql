package cn.oyzh.easymysql.exception;

/**
 * db异常
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class DBException extends RuntimeException {

    /**
     * 构造方法
     */
    public DBException() {
        super();
    }

    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public DBException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param ex 异常对象
     */
    public DBException(Exception ex) {
        super(ex);
    }
}
