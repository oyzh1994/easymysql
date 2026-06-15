package cn.oyzh.easymysql.terminal;

import cn.oyzh.easymysql.terminal.basic.MysqlShowDatabasesTerminalCommandHandler;
import cn.oyzh.easymysql.terminal.basic.MysqlShowDbsTerminalCommandHandler;
import cn.oyzh.easymysql.terminal.basic.MysqlShowTablesTerminalCommandHandler;
import cn.oyzh.easymysql.terminal.basic.MysqlUseTerminalCommandHandler;
import cn.oyzh.fx.terminal.standard.ClearTerminalCommandHandler;
import cn.oyzh.fx.terminal.standard.HelpTerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;

/**
 * @author oyzh
 * @since 2024-12-30
 */
public class MysqlTerminalManager {

    /**
     * 注册处理器
     */
    public static void registerHandlers() {
        // 标准命令
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, HelpTerminalCommandHandler.class);
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, ClearTerminalCommandHandler.class);

        // 基础命令
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, MysqlShowDatabasesTerminalCommandHandler.class);
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, MysqlShowDbsTerminalCommandHandler.class);
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, MysqlShowTablesTerminalCommandHandler.class);
        TerminalManager.registerHandler(MysqlTerminalPane.TERMINAL_NAME, MysqlUseTerminalCommandHandler.class);
    }
}
