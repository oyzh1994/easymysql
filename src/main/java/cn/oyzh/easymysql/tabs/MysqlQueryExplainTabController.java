package cn.oyzh.easymysql.tabs;

import cn.oyzh.easymysql.db.query.MysqlExplainResult;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.db.table.MysqlColumn;
import cn.oyzh.easymysql.fx.DBStatusColumn;
import cn.oyzh.easymysql.fx.record.DBRecordColumn;
import cn.oyzh.easymysql.fx.record.DBRecordTableView;
import cn.oyzh.easymysql.util.DBRecordUtil;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/16
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class MysqlQueryExplainTabController extends DynamicTabController {

    /**
     * sql组件
     */
    @FXML
    private FlexText sql;

    /**
     * 耗时组件
     */
    @FXML
    private FlexText used;

    /**
     * 计数组件
     */
    @FXML
    private FlexText count;

    /**
     * 数据表单组件
     */
    @FXML
    private DBRecordTableView recordTable;

    /**
     * 执行结果
     */
    private MysqlExplainResult result;

    /**
     * 执行初始化
     *
     * @param result 执行结果
     */
    public void init(MysqlExplainResult result ) {
        this.result = result;
        this.initDataList();
    }

    /**
     * 初始化数据列表
     */
    private void initDataList() {
        try {
            // 初始化字段
            this.initColumns(this.result.columnList());
            // 初始化数据
            this.initRecords(this.result.records());
            // 初始化sql信息
            this.sql.setText(this.result.sql());
            this.used.setText(I18nHelper.time() + ": " + this.result.getUsedMs() + "ms");
            this.count.setText(I18nHelper.totalData() + ": " + this.result.getCount());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 初始化列
     *
     * @param columns 列数据
     */
    private void initColumns(List<MysqlColumn> columns) {
        // 数据列集合
        List<FlexTableColumn<MysqlRecord, Object>> columnList = new ArrayList<>();
        DBStatusColumn<MysqlRecord> statusColumn = new DBStatusColumn<>();
        columnList.add(statusColumn);
        for (MysqlColumn column : columns) {
            DBRecordColumn tableColumn = new DBRecordColumn(column);
            tableColumn.setRealWidth(DBRecordUtil.suitableColumnWidth(column.getType()));
            columnList.add(tableColumn);
        }
        this.recordTable.getColumns().setAll(columnList);
    }

    /**
     * 初始化记录
     *
     * @param records 数据
     */
    private void initRecords(List<MysqlRecord> records) {
        this.recordTable.setItem(records);
    }
}
