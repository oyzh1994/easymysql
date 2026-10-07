package cn.oyzh.easymysql.terminal;

import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;

/**
 * mysql终端命令处理器
 *
 * @author oyzh
 * @since 2024-12-30
 */
public abstract class MysqlTerminalCommandHandler<C extends TerminalCommand> extends BaseTerminalCommandHandler<C, MysqlTerminalPane> {

}
