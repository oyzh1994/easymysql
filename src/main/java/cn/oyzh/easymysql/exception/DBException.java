package cn.oyzh.easymysql.exception;

/**
 * redis异常
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class DBException extends RuntimeException {

    public DBException() {
        super();
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(Exception ex) {
        super(ex);
    }
}
