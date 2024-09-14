package cn.oyzh.easymysql.util;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.record.MysqlRecordProperty;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.fx.record.DBBinaryTextFiled;
import cn.oyzh.easymysql.fx.record.DBJsonTextFiled;
import cn.oyzh.fx.plus.controls.select.SelectTextFiled;
import cn.oyzh.fx.plus.controls.textfield.BitTextField;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.DateTextField;
import cn.oyzh.fx.plus.controls.textfield.DateTimeTextField;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.controls.textfield.ExampleTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.textfield.TimeTextField;
import cn.oyzh.fx.plus.controls.textfield.YearTextField;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/7/17
 */
@UtilityClass
public class DBRecordUtil {

    public static Node getNode(MysqlRecordProperty property, Object object, MysqlColumn column) {
        Node node;
        String columnType = column.getType();
        if (column.supportJson()) {
            DBJsonTextFiled textField = new DBJsonTextFiled();
            textField.setValue(object);
            node = textField;
        } else if (column.supportBinary()) {
            DBBinaryTextFiled textField = new DBBinaryTextFiled(columnType);
            textField.setValue(object);
            node = textField;
        } else if (column.supportEnum()) {
            SelectTextFiled textField = new SelectTextFiled();
            textField.setEditable(false);
            textField.setDataList(column.getValueList());
            textField.setValue(object);
            node = textField;
        } else if (column.supportInteger()) {
            NumberTextField textField = new NumberTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportDigits()) {
            DecimalTextField textField = new DecimalTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportBit()) {
            BitTextField textField = new BitTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isDateType()) {
            DateTextField textField = new DateTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isTimeType()) {
            TimeTextField textField = new TimeTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.isYearType()) {
            YearTextField textField = new YearTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportTimestamp()) {
            DateTimeTextField textField = new DateTimeTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportString()) {
            ClearableTextField textField = new ClearableTextField();
            textField.setValue(object);
            node = textField;
        } else if (column.supportGeometry()) {
            ExampleTextField textField = new ExampleTextField();
            textField.setExample(column.exampleValue());
            textField.setValue(object);
            node = textField;
        } else {
            ClearableTextField textField = new ClearableTextField();
            textField.setValue(object);
            node = textField;
        }
        if (node instanceof TextField textField) {
            if (object == null) {
                textField.setPromptText(nullPromptText());
            }
            textField.setContextMenu(getColumnContextMenu(property));
            textField.textProperty().addListener((observable, oldValue, newValue) -> property.setChanged(true));
        }
        return node;
    }

    public static String formatValue(Object object, MysqlColumn column) {
        String val = null;
        String columnType = column.getType();
        if (StrUtil.isBlank(columnType)) {
            if (object instanceof CharSequence sequence) {
                val = sequence.toString();
            } else if (object instanceof byte[] bytes) {
                val = new String(bytes);
            } else if (object instanceof Date date) {
                val = date.toString();
            } else if (object != null) {
                val = object.toString();
            }
        } else if (column.supportJson()) {
            val = DBJsonTextFiled.format(object);
        } else if (column.supportBinary()) {
            if (object instanceof byte[] bytes) {
                val = DBBinaryTextFiled.format(columnType, bytes);
            }
        } else if (column.supportEnum()) {
            val = SelectTextFiled.format(object);
        } else if (column.supportInteger()) {
            val = NumberTextField.format(object);
        } else if (column.supportDigits()) {
            val = DecimalTextField.format(object);
        } else if (column.supportBit()) {
            val = BitTextField.format(object);
        } else if (column.isDateType()) {
            val = DateTextField.format(object);
        } else if (column.isTimeType()) {
            val = TimeTextField.format(object);
        } else if (column.isYearType()) {
            val = YearTextField.format(object);
        } else if (column.supportTimestamp()) {
            val = DateTimeTextField.format(object);
        } else if (column.supportString()) {
            val = ClearableTextField.format(object);
        } else if (column.supportGeometry()) {
            val = ExampleTextField.format(object);
        } else {
            val = ClearableTextField.format(object);
        }
        return val;
    }

    public static String nullPromptText() {
        return "(Null)";
    }

    public static double suitableColumnWidth(String columnType) {
        if (DBColumnUtil.isGeometryType(columnType)) {
            return 120;
        }
        if (DBColumnUtil.isPointType(columnType)) {
            return 110;
        }
        if (DBColumnUtil.isMultiPointType(columnType)) {
            return 200;
        }
        if (DBColumnUtil.isPolygonType(columnType)) {
            return 220;
        }
        if (DBColumnUtil.isMultiPolygonType(columnType)) {
            return 420;
        }
        if (DBColumnUtil.isLineStringType(columnType)) {
            return 180;
        }
        if (DBColumnUtil.isMultiLineStringType(columnType)) {
            return 320;
        }
        if (DBColumnUtil.isGeomCollectionType(columnType)) {
            return 600;
        }
        if (DBColumnUtil.isYearType(columnType)) {
            return 80;
        }
        if (DBColumnUtil.supportJson(columnType)) {
            return 150;
        }
        if (DBColumnUtil.supportTimestamp(columnType)) {
            return 160;
        }
        if (DBColumnUtil.supportBinary(columnType)) {
            return 140;
        }
        if (DBColumnUtil.isDateType(columnType)) {
            return 110;
        }
        return 100;
    }

    public static ContextMenu getColumnContextMenu(MysqlRecordProperty property) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(getColumnMenuItem(property));
        return contextMenu;
    }

    public static List<FXMenuItem> getColumnMenuItem(MysqlRecordProperty property) {
        List<FXMenuItem> menuItems = new ArrayList<>();
        FXMenuItem copy = MenuItemHelper.copy(property::vCopy);
        FXMenuItem paste = MenuItemHelper.paste(property::vPaste);
        FXMenuItem delete = MenuItemHelper.deleteRecord(property::vDelete);
        FXMenuItem setToNull = MenuItemHelper.setToNull(property::vSetToNull);
        FXMenuItem setToEmptyString = MenuItemHelper.setToEmptyString(property::vSetToEmptyString);
        menuItems.add(copy);
        menuItems.add(paste);
        menuItems.add(setToNull);
        menuItems.add(setToEmptyString);
        menuItems.add(delete);
        return menuItems;
    }
}
