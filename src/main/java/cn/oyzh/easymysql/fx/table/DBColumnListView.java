package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FlexListView;
import cn.oyzh.fx.plus.util.ListViewUtil;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db字段选择框
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class DBColumnListView extends FlexListView<FXCheckBox> {

    public DBColumnListView() {

    }

    public DBColumnListView(List<MysqlColumn> columns) {
        this.init(columns);
    }

    public void init(List<MysqlColumn> columns) {
        this.init(columns, null);
    }

    public void init(List<MysqlColumn> columns, List<String> selectedColumns) {
        this.clearItems();
        if (CollUtil.isNotEmpty(columns)) {
            for (MysqlColumn column : columns) {
                boolean selected = CollUtil.contains(selectedColumns, column.getName());
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setSelected(selected);
                checkBox.setText(column.getName());
                checkBox.setProp("column", column);
                ListViewUtil.selectRowOnMouseClicked(checkBox);
                this.addItem(checkBox);
            }
        }
    }

    public List<MysqlColumn> getSelectedColumns() {
        List<FXCheckBox> checkBoxes = this.getItems().parallelStream().filter(CheckBox::isSelected).toList();
        List<MysqlColumn> columns = new ArrayList<>();
        for (FXCheckBox checkBox : checkBoxes) {
            columns.add(checkBox.getProp("column"));
        }
        return columns;
    }

    public List<String> getSelectedColumnNames() {
        List<MysqlColumn> columns = this.getSelectedColumns();
        return columns.parallelStream().map(MysqlColumn::getName).collect(Collectors.toList());
    }

    public void select(Collection<String> columns) {
        if (CollUtil.isNotEmpty(columns)) {
            for (FXCheckBox checkBox : this.getItems()) {
                MysqlColumn column = checkBox.getProp("column");
                for (String s : columns) {
                    if (StrUtil.equalsIgnoreCase(s.trim(), column.getName())) {
                        checkBox.setSelected(true);
                        break;
                    }
                }
            }
        }
    }
}
