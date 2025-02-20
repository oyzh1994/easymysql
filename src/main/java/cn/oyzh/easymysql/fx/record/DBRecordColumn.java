package cn.oyzh.easymysql.fx.record;

import atlantafx.base.controls.Popover;
import cn.oyzh.easymysql.db.column.MysqlColumn;
import cn.oyzh.easymysql.db.record.MysqlRecord;
import cn.oyzh.easymysql.popups.MysqlFieldInfoPopupController;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import cn.oyzh.fx.plus.menu.FXContextMenu;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.mouse.MouseUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import javafx.stage.PopupWindow;

/**
 * @author oyzh
 * @since 2024/7/17
 */
public class DBRecordColumn extends FXTableColumn<MysqlRecord, Object> {

    public DBRecordColumn(MysqlColumn column) {
        this.setText(column.getName());
        this.setCellValueFactory(p -> p.getValue().getProperty(column.getName()));
        SVGGlyph info = new SVGGlyph("/font/tableField.svg", "12");
        info.setOnMousePrimaryClicked(event -> {
            event.consume();
            this.showColumnInfo(column);
        });
        this.setGraphic(info);
        FXMenuItem fieldInfo = MenuItemHelper.fieldInfo(() -> this.showColumnInfo(column));
        this.setContextMenu(new FXContextMenu(fieldInfo));
    }

    private void showColumnInfo(MysqlColumn column) {
        PopupAdapter popup = PopupManager.parsePopup(MysqlFieldInfoPopupController.class, Popover.ArrowLocation.TOP_LEFT, PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        popup.setProp("column", column);
        popup.showPopup(this.getGraphic(), MouseUtil.getMouseX(), MouseUtil.getMouseY());
    }
}
