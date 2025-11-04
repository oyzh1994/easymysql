package cn.oyzh.easymysql.fx.record;

import atlantafx.base.controls.Popover;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.popups.MysqlFieldInfoPopupController;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.database.ColumnSVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import cn.oyzh.fx.plus.menu.FXContextMenu;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.mouse.MouseUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import javafx.stage.PopupWindow;

/**
 * @author oyzh
 * @since 2024/7/17
 */
public class DBRecordColumn extends FXTableColumn<MysqlRecord, Object> {

    public DBRecordColumn(MysqlColumn column) {
        this.setReorderable(true);
        this.setText(column.getName());
        this.setCellValueFactory(p -> p.getValue().getProperty(column.getName()));
        ColumnSVGGlyph svgGlyph = new ColumnSVGGlyph("12");
        // SVGGlyph info = new SVGGlyph("/font/tableField.svg", "12");
        // svgGlyph.setOnMousePrimaryClicked(event -> {
        //     this.showColumnInfo(column);
        //     event.consume();
        // });
        this.setGraphic(svgGlyph);
        FXContextMenu menu = new FXContextMenu();
        FXMenuItem fieldInfo = MenuItemHelper.columnInfo(() -> this.showColumnInfo(column));
        menu.addItem(fieldInfo);
        FXMenuItem copyFieldName = MenuItemHelper.copyColumnName(() -> this.copyColumnName(column));
        menu.addItem(copyFieldName);
        this.setContextMenu(menu);
    }

    /**
     * 显示字段信息
     *
     * @param column 字段
     */
    private void showColumnInfo(MysqlColumn column) {
        PopupAdapter popup = PopupManager.parsePopup(MysqlFieldInfoPopupController.class, Popover.ArrowLocation.TOP_LEFT, PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        popup.setProp("column", column);
        popup.showPopup(this.getGraphic(), MouseUtil.getMouseX(), MouseUtil.getMouseY());
    }

    /**
     * 复制字段名称
     *
     * @param column 字段
     */
    private void copyColumnName(MysqlColumn column) {
        ClipboardUtil.copy(column.getName());
    }
}
