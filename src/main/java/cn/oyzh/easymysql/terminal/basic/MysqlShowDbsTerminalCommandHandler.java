package cn.oyzh.easymysql.terminal.basic;

/**
 * show dbs命令处理器
 *
 * @author oyzh
 * @since 2024-12-30
 */
public class MysqlShowDbsTerminalCommandHandler extends MysqlShowDatabasesTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "dbs;";
    }
}
