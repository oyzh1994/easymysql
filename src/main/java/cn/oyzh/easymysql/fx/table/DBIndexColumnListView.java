package cn.oyzh.easymysql.fx.table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.db.table.DBIndex;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.view.FlexListView;
import cn.oyzh.fx.plus.util.ListViewUtil;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db索引字段选择框
 *
 * @author oyzh
 * @since 2024/07/16
 */
public class DBIndexColumnListView extends FlexListView<FlexHBox> {

    /**
     * 字段名称列表
     */
    private List<String> columnNames;

    public DBIndexColumnListView() {

    }

    public void init(DBIndex dbIndex, List<DBColumn> columnList) {
        this.clearItems();
        this.columnNames = columnList.parallelStream().map(DBColumn::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(dbIndex.getColumns())) {
            for (DBIndex.IndexColumn column : dbIndex.getColumns()) {
                this.addColumn(column);
            }
        }
    }

    public void addColumn(DBIndex.IndexColumn column) {
        FlexComboBox<String> comboBox = new FlexComboBox<>();
        comboBox.setRealWidth(150);
        comboBox.setRealHeight(25);
        comboBox.setItem(this.columnNames);
        comboBox.addClass("popover-item");
        if (StrUtil.isNotBlank(column.getColumnName())) {
            comboBox.select(column.getColumnName());
        } else {
            comboBox.selectFirst();
        }

        NumberTextField textField = new NumberTextField();
        textField.setMinVal(0);
        textField.setRealHeight(25);
        textField.setRealWidth(145);
        textField.addClass("popover-item");
        if (column.getSubPart() > 0) {
            textField.setValue(column.getSubPart());
        }
        FlexHBox hBox = new FlexHBox(comboBox, textField);
        HBox.setMargin(textField, new Insets(0, 0, 0, 5));
        ListViewUtil.selectRowOnMouseClicked(comboBox, hBox);
        ListViewUtil.selectRowOnMouseClicked(textField, hBox);
        this.addItem(hBox);
    }

    public List<DBIndex.IndexColumn> getColumns() {
        List<DBIndex.IndexColumn> list = new ArrayList<>();
        for (FlexHBox item : this.getItems()) {
            FlexComboBox<String> comboBox = (FlexComboBox) item.getChild(0);
            NumberTextField textField = (NumberTextField) item.getChild(1);
            DBIndex.IndexColumn indexColumn = new DBIndex.IndexColumn(comboBox.getValue(), textField.getIntValue());
            list.add(indexColumn);
        }
        return list;
    }
}
