package cn.oyzh.easymysql.db;

/**
 * db连接状态
 *
 * @author oyzh
 * @since 2023/07/1
 */
public enum DBConnState {

    /**
     * 未初始化
     */
    NOT_INITIALIZED {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 已连接
     */
    CONNECTED {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return true;
        }
    },
    /**
     * 连接中
     */
    CONNECTING {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 已关闭
     */
    CLOSED {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 失败
     */
    FAILED {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 错误
     */
    BROKEN {
        /**
         * 是否已连接
         *
         * @return 结果
         */
        public boolean isConnected() {
            return false;
        }
    };

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public abstract boolean isConnected();
}
