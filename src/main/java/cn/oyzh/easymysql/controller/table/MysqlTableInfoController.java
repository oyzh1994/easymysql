package cn.oyzh.easymysql.controller.table;

import cn.oyzh.easymysql.db.table.MysqlTable;
import cn.oyzh.easymysql.trees.table.MysqlTableTreeItem;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.FXGroup;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.rich.richtextfx.control.FlexRichTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * db表信息业务
 *
 * @author oyzh
 * @since 2024/01/30
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "table/mysqlTableInfo.fxml"
)
public class MysqlTableInfoController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField tableName;

    /**
     * 引擎
     */
    @FXML
    private ReadOnlyTextField tableEngine;

    /**
     * 字符集
     */
    @FXML
    private ReadOnlyTextField tableCharset;

    /**
     * 排序方式
     */
    @FXML
    private ReadOnlyTextField tableCollation;

    /**
     * 行格式组件
     */
    @FXML
    private FXVBox tableRowFormatBox;

    /**
     * 行格式
     */
    @FXML
    private ReadOnlyTextField tableRowFormat;

    /**
     * 自动递增组件
     */
    @FXML
    private FXVBox tableAutoIncrementBox;

    /**
     * 自动递增
     */
    @FXML
    private ReadOnlyTextField tableAutoIncrement;

    /**
     * 注释
     */
    @FXML
    private ReadOnlyTextArea tableComment;

    /**
     * 定义
     */
    @FXML
    private FlexRichTextArea tableDefinition;

    @Override
    public void onWindowShown(WindowEvent event) {
        MysqlTableTreeItem tableItem = this.getWindowProp("tableItem");
        MysqlTable table = tableItem.value();
        this.tableName.setText(table.getName());
        this.tableEngine.setText(table.getEngine());
        this.tableComment.setText(table.getComment());
        this.tableCharset.setText(table.getCharset());
        this.tableCollation.setText(table.getCollation());
        this.tableDefinition.setText(table.getCreateDefinition());
        if (table.isInnoDB()) {
            this.tableRowFormatBox.display();
            this.tableRowFormat.setText(table.getRowFormat());
        }
        if (table.hasAutoIncrement()) {
            this.tableAutoIncrementBox.display();
            this.tableAutoIncrement.setText(table.getAutoIncrement() + "");
        }
        super.onWindowShown(event);
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        super.onStageInitialize(stage);
        // 组件管理
        this.tableRowFormatBox.managedBindVisible();
        this.tableAutoIncrementBox.managedBindVisible();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.tableInfo();
    }
}
