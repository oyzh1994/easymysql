package cn.oyzh.easymysql.fx;

import cn.oyzh.fx.plus.controls.tab.FXTabPane;

/**
 * mysql设计选项卡面板
 *
 * @author oyzh
 * @since 2025-10-31
 */
public class MysqlDesignTabPane extends FXTabPane {

    @Override
    public void initNode() {
        super.initNode();
        // this.setupSelectCountListener();
        this.setupRefreshListener();
    }
}
