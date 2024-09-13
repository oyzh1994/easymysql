package cn.oyzh.easymysql.handler.dump;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.db.DBClient;
import cn.oyzh.easymysql.db.event.MysqlEvent;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.routine.DBFunction;
import cn.oyzh.easymysql.db.routine.DBProcedure;
import cn.oyzh.easymysql.db.table.DBColumns;
import cn.oyzh.easymysql.db.table.DBTable;
import cn.oyzh.easymysql.db.table.DBTrigger;
import cn.oyzh.easymysql.db.view.DBView;
import cn.oyzh.easymysql.util.DBDataUtil;
import cn.oyzh.easymysql.util.DBUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/10
 */
@Slf4j
public class MysqlDataDumpHandler extends DataDumpHandler {

    public MysqlDataDumpHandler(DBClient dbClient, String dbName) {
        super(dbClient, dbName);
    }

    @Override
    public void doDump() throws Exception {
        if (this.fileWriter == null || this.dumpType == null || this.dataType == null) {
            throw new RuntimeException("parameter invalid!");
        }
        this.message("Dump Starting");
        this.writeHeader();
        if (this.dumpType == 1) {
            this.dumpTable();
            this.dumpView();
            this.dumpFunction();
            this.dumpProcedure();
            this.dumpTrigger();
            this.dumpEvent();
        } else if (this.dumpType == 2) {
            DBTable table = this.dbClient.table(this.dbName, this.tableName);
            this.dumpTable(table);
        }
        this.writeTail();
        this.fileWriter.close();
        this.message("Dump Finished");
        this.message("Dump File To -> " + this.dumpFile.getPath());
    }

    protected void dumpTable() throws InterruptedException, IOException {
        List<DBTable> tables = this.dbClient.tables(this.dbName);
        if (CollUtil.isNotEmpty(tables)) {
            for (DBTable table : tables) {
                this.checkInterrupt();
                this.dumpTable(table);
            }
            this.processed(tables.size());
        }
    }

    protected void dumpTable(DBTable table) throws InterruptedException, IOException {
        String line0 = "";
        String line1 = "-- ----------------------------";
        String line2 = "-- Table structure for " + table.getName();
        String line3 = "-- ----------------------------";
        String dropTable = "DROP TABLE IF EXISTS " + DBUtil.wrap(table.getName()) + ";";
        String createDefinition = table.getCreateDefinition();
        if (!createDefinition.endsWith(";")) {
            createDefinition += ";";
        }
        this.message("Dumping Table " + table.getName());
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTable, createDefinition));
        if (this.isDumpRecord()) {
            this.message("Dumping Records of Table " + table.getName());
            this.dumpRecord(table.getName());
        }
    }

    protected void dumpRecord(String tableName) throws InterruptedException, IOException {
        long start = 0;
        String line0 = "";
        String line1 = "-- ----------------------------";
        String line2 = "-- Records of " + tableName;
        String line3 = "-- ----------------------------";
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3));
        DBColumns columns = new DBColumns(this.dbClient.tableColumns(this.dbName, null, tableName));
        while (true) {
            this.checkInterrupt();
            long start1 = System.currentTimeMillis();
            List<MysqlRecord> records = this.dbClient.selectTableRecords(this.dbName, tableName, start, (long) this.queryLimit, columns, null, true);
            if (CollUtil.isEmpty(records)) {
                break;
            }
            long end1 = System.currentTimeMillis();
            log.info("查询耗时: {}ms", (end1 - start1));
            long start2 = System.currentTimeMillis();
            List<String> inserts = DBDataUtil.toInsertSql(columns, records);
            this.fileWriter.appendLines(inserts);
            long end2 = System.currentTimeMillis();
            log.info("写入耗时: {}ms", (end2 - start2));
            start += this.queryLimit;
            this.processed(records.size());
        }
    }

    protected void dumpView() throws Exception {
        List<DBView> views = this.dbClient.views(this.dbName);
        if (CollUtil.isNotEmpty(views)) {
            for (DBView view : views) {
                this.checkInterrupt();
                this.message("Dumping View " + view.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- View structure for " + view.getName();
                String line3 = "-- ----------------------------";
                String dropTable = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getName()) + ";";
                String createDefinition = this.dbClient.showCreateView(this.dbName, view.getName());
                if (!createDefinition.endsWith(";")) {
                    createDefinition += ";";
                }
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTable, createDefinition));
            }
            this.processed(views.size());
        }
    }

    protected void dumpFunction() throws Exception {
        List<DBFunction> functions = this.dbClient.functions(this.dbName);
        if (CollUtil.isNotEmpty(functions)) {
            for (DBFunction function : functions) {
                this.checkInterrupt();
                this.message("Dumping Function " + function.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Function structure for " + function.getName();
                String line3 = "-- ----------------------------";
                String dropFunction = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(function.getName()) + ";";
                String line4 = "delimiter ;;";
                String line5 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateFunction(this.dbName, function.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropFunction, line4, createDefinition, line5));
            }
            this.processed(functions.size());
        }
    }

    protected void dumpProcedure() throws Exception {
        List<DBProcedure> procedures = this.dbClient.procedures(this.dbName);
        if (CollUtil.isNotEmpty(procedures)) {
            for (DBProcedure procedure : procedures) {
                this.checkInterrupt();
                this.message("Dumping Procedure " + procedure.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Procedure structure for " + procedure.getName();
                String line3 = "-- ----------------------------";
                String dropProcedure = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(procedure.getName()) + ";";
                String line4 = "delimiter ;;";
                String line5 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateProcedure(this.dbName, procedure.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropProcedure, line4, createDefinition, line5));
            }
            this.processed(procedures.size());
        }
    }

    protected void dumpTrigger() throws Exception {
        List<DBTrigger> triggers = this.dbClient.triggers(this.dbName);
        if (CollUtil.isNotEmpty(triggers)) {
            for (DBTrigger trigger : triggers) {
                this.message("Dumping Trigger " + trigger.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Trigger structure for " + trigger.getName();
                String line3 = "-- ----------------------------";
                String dropTrigger = "DROP TRIGGER IF EXISTS " + DBUtil.wrap(trigger.getName()) + ";";
                String line4 = "delimiter ;;";
                String line5 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateTrigger(this.dbName, trigger.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTrigger, line4, createDefinition, line5));
            }
            this.processed(triggers.size());
        }
    }

    protected void dumpEvent() throws Exception {
        List<MysqlEvent> events = this.dbClient.events(this.dbName);
        if (CollUtil.isNotEmpty(events)) {
            for (MysqlEvent event : events) {
                this.message("Dumping Event " + event.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Event structure for " + event.getName();
                String line3 = "-- ----------------------------";
                String dropTrigger = "DROP EVENT IF EXISTS " + DBUtil.wrap(event.getName()) + ";";
                String line4 = "delimiter ;;";
                String line5 = "delimiter ;";
                String createDefinition = event.getCreateDefinition();
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTrigger, line4, createDefinition, line5));
            }
            this.processed(events.size());
        }
    }
}

