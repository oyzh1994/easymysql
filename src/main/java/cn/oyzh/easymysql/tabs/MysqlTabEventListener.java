package cn.oyzh.easymysql.tabs;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.domain.DBQuery;
import cn.oyzh.easymysql.event.DBConnectionClosedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlDatabaseClosedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlEventDesignEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlFunctionDesignEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlProcedureDesignEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlQueryAddEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlQueryDeletedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlQueryOpenEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableAlertedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableClearedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableDesignEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableDroppedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableFilteredEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableOpenEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableRenamedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlTableTruncatedEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlViewDesignEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlViewFilteredEvent;
import cn.oyzh.easymysql.module.mysql.event.MysqlViewOpenEvent;
import cn.oyzh.easymysql.trees.MysqlDatabaseTreeItem;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.information.MessageBox;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-12
 */
public class MysqlTabEventListener implements EventListener {

    private DBTabPane tabPane;

    public MysqlTabEventListener(DBTabPane tabPane) {
        super();
        this.tabPane = tabPane;
        EventListener.super.register();
    }

    @Override
    protected void finalize() throws Throwable {
        this.tabPane = null;
        EventListener.super.unregister();
        super.finalize();
    }

    private List<Tab> getTabs() {
        return this.tabPane.getTabs();
    }

    private void addTab(Tab tab) {
        this.tabPane.addTab(tab);
    }

    private void select(Tab tab) {
        this.tabPane.select(tab);
    }

    private void removeTab(Tab tab) {
        this.tabPane.removeTab(tab);
    }

    private void removeTab(List<? extends Tab> tab) {
        this.tabPane.removeTab(tab);
    }

    /**
     * 获取tab列表
     *
     * @return tab列表
     */
    public List<MysqlTab> getMysqlTabs() {
        List<MysqlTab> list = new ArrayList<>();
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlTab tab1) {
                list.add(tab1);
            }
        }
        return list;
    }

    private List<MysqlTab> getMysqlTabs(MysqlDatabaseTreeItem dbItem) {
        List<MysqlTab> list = new ArrayList<>();
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlTab tab1 && tab1.dbItem() == dbItem) {
                list.add(tab1);
            }
        }
        return list;
    }

    private MysqlTableRecordTab getMysqlTableRecordTab(MysqlDatabaseTreeItem dbItem, String tableName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlTableRecordTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(tableName, tab1.tableName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 表打开事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableOpen(MysqlTableOpenEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tab == null) {
                tab = new MysqlTableRecordTab();
                this.addTab(tab);
            }
            this.select(tab);
            tab.init(event.data());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表重命名事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableRenamed(MysqlTableRenamedEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tab != null) {
                tab.flushTitle();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表清空事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableCleared(MysqlTableClearedEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tab != null) {
                tab.reload();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表截断事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableTruncated(MysqlTableTruncatedEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tab != null) {
                tab.reload();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表删除事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableDropped(MysqlTableDroppedEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tab != null) {
                tab.closeTab();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表过滤事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableFiltered(MysqlTableFilteredEvent event) {
        try {
            MysqlTableRecordTab tableTab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
            if (tableTab != null) {
                tableTab.setFilters(event.filters());
                tableTab.reload();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 表变更事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableAlerted(MysqlTableAlertedEvent event) {
        try {
            MysqlTableRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.data());
            if (tab != null) {
                tab.flush();
                tab.reload();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlViewRecordTab getMysqlViewRecordTab(MysqlDatabaseTreeItem dbItem, String viewName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlViewRecordTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(tab1.viewName(), viewName)) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 视图打开事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlViewOpen(MysqlViewOpenEvent event) {
        try {
            MysqlViewRecordTab tab = this.getMysqlViewRecordTab(event.dbItem(), event.viewName());
            if (tab == null) {
                tab = new MysqlViewRecordTab();
                this.addTab(tab);
            }
            this.select(tab);
            tab.init(event.data());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 视图过滤事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlViewFiltered(MysqlViewFilteredEvent event) {
        try {
            MysqlViewRecordTab viewRecordTab = this.getMysqlViewRecordTab(event.dbItem(), event.viewName());
            if (viewRecordTab != null) {
                viewRecordTab.setFilters(event.filters());
                viewRecordTab.reload();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlQueryMainTab getMysqlQueryMainTab(String queryId) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlQueryMainTab tab1 && StrUtil.equals(tab1.queryId(), queryId)) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 查询新增事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlQueryAdd(MysqlQueryAddEvent event) {
        try {
            MysqlQueryMainTab tab = new MysqlQueryMainTab();
            this.addTab(tab);
            this.select(tab);
            DBQuery query = new DBQuery();
            tab.init(query, event.data());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 查询删除事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlQueryDeleted(MysqlQueryDeletedEvent event) {
        try {
            MysqlQueryMainTab tab = this.getMysqlQueryMainTab(event.queryId());
            if (tab != null) {
                this.removeTab(tab);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 查询打开事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlQueryOpen(MysqlQueryOpenEvent event) {
        try {
            MysqlQueryMainTab tab = this.getMysqlQueryMainTab(event.queryId());
            if (tab == null) {
                tab = new MysqlQueryMainTab();
                tab.init(event.data(), event.item());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 数据库关闭事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlDatabaseClosed(MysqlDatabaseClosedEvent event) {
        this.removeTab(this.getMysqlTabs(event.data()));
    }

    private MysqlFunctionDesignTab getMysqlFunctionTab(MysqlDatabaseTreeItem dbItem, String functionName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlFunctionDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(functionName, tab1.functionName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 函数设计事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlFunctionDesign(MysqlFunctionDesignEvent event) {
        try {
            MysqlFunctionDesignTab tab = this.getMysqlFunctionTab(event.dbItem(), event.functionName());
            if (tab == null) {
                tab = new MysqlFunctionDesignTab();
                tab.init(event.data(), event.dbItem());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlProcedureDesignTab getMysqlProcedureTab(MysqlDatabaseTreeItem dbItem, String procedureName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlProcedureDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(procedureName, tab1.procedureName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 过程设计事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlProcedureDesign(MysqlProcedureDesignEvent event) {
        try {
            MysqlProcedureDesignTab tab = this.getMysqlProcedureTab(event.dbItem(), event.procedureName());
            if (tab == null) {
                tab = new MysqlProcedureDesignTab();
                tab.init(event.data(), event.dbItem());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlEventDesignTab getMysqlEventTab(MysqlDatabaseTreeItem dbItem, String eventName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlEventDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(eventName, tab1.eventName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 事件设计事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlEventDesign(MysqlEventDesignEvent event) {
        try {
            MysqlEventDesignTab tab = this.getMysqlEventTab(event.dbItem(), event.eventName());
            if (tab == null) {
                tab = new MysqlEventDesignTab();
                tab.init(event.data(), event.dbItem());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlViewDesignTab getMysqlViewDesignTab(MysqlDatabaseTreeItem dbItem, String viewName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlViewDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(viewName, tab1.viewName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 视图设计事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlViewDesign(MysqlViewDesignEvent event) {
        try {
            MysqlViewDesignTab tab = this.getMysqlViewDesignTab(event.dbItem(), event.viewName());
            if (tab == null) {
                tab = new MysqlViewDesignTab();
                tab.init(event.data(), event.dbItem());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlTableDesignTab getMysqlTableDesignTab(MysqlDatabaseTreeItem dbItem, String tableName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MysqlTableDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equalsIgnoreCase(tableName, tab1.tableName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 表设计事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onMysqlTableDesign(MysqlTableDesignEvent event) {
        try {
            MysqlTableDesignTab tab = this.getMysqlTableDesignTab(event.dbItem(), event.tableName());
            if (tab == null) {
                tab = new MysqlTableDesignTab();
                tab.init(event.data(), event.dbItem());
                this.addTab(tab);
            }
            this.select(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 连接关闭事件
     *
     * @param event 事件
     */
    @Subscribe
    private void onConnectionClosed(DBConnectionClosedEvent event) {
        this.removeTab(this.getMysqlTabs());
    }
}
