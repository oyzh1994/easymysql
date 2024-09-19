package cn.oyzh.easymysql.fx;

import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author oyzh
 * @since 2024/7/22
 */
public class DBStatusColumn<S extends DBObjectStatus> extends FlexTableColumn<S, Object> {

    public DBStatusColumn() {
        this.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.setMaxWidth(25);
        this.setRealWidth(25);
        this.setSortable(false);
        this.setResizable(false);
        this.setReorderable(false);
    }
}
