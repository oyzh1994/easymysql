package cn.oyzh.easymysql.terminal.basic;

/**
 * @author oyzh
 * @since 2024-12-30
 */
public class MysqlShowDbsTerminalCommandHandler extends MysqlShowDatabasesTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "dbs;";
    }
}
