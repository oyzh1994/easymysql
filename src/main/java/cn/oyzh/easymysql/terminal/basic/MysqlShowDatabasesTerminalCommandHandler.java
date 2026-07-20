package cn.oyzh.easymysql.terminal.basic;

import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.easymysql.db.DBDatabase;
import cn.oyzh.easymysql.terminal.MysqlTerminalCommandHandler;
import cn.oyzh.easymysql.terminal.MysqlTerminalPane;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024-12-30
 */
public class MysqlShowDatabasesTerminalCommandHandler extends MysqlTerminalCommandHandler<TerminalCommand> {

    @Override
    protected TerminalCommand parseCommand(String line, String[] args) {
        TerminalCommand terminalCommand = new TerminalCommand();
        terminalCommand.setArgs(args);
        terminalCommand.setCommand(line);
        return terminalCommand;
    }

    @Override
    protected boolean checkArgs(String[] args) throws RuntimeException {
        return args != null && args.length == 2;
    }

    @Override
    public String commandName() {
        return "show";
    }

    @Override
    public String commandSubName() {
        return "databases;";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, MysqlTerminalPane terminal) {
        TerminalExecuteResult result = TerminalExecuteResult.ok();
        try {
            List<DBDatabase> databases = terminal.getClient().databases();
            String output = databases.stream()
                    .map(DBDatabase::getName)
                    .collect(Collectors.joining(terminal.lineEndingText()));
            result.setResult(output);
        } catch (Exception ex) {
            result.setException(ex);
        }
        return result;
    }
}
